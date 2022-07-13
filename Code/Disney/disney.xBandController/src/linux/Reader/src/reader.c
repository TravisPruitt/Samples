#include "reader-private.h"
#include "priority.h"
#include "updatestream.h"

#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <libxml/parser.h>
#include <libxml/tree.h>
#include <assert.h>

#define DESIRED_QUEUE_SIZE 20000


struct Readers
{
    char *pszControllerName;
    char *pszControllerURL;
    char *pszXBRMSURL;
    long long start;
    long long end;
};


int SubmitEvent(EVENTINFO *ei, void *pvCallbackData)
{
    struct Reader *pReader = pvCallbackData;
    size_t sEvents = 0;

    if (ei != NULL)
    {
        assert(Enqueue(pReader->pQueue, ei) == 0);
    }

    sEvents = EventCount(pReader->pQueue);
    if (sEvents >= DESIRED_QUEUE_SIZE)
        return 1;
    else
        return 0;
}



int
CreateReader(
    const char *pszName,
    enum DeviceType deviceType,
    const char *pszHardwareAddress,
    const char *pszHardwareType,
    uint16_t uPort,
    const char *pszMac,
    const char *pszControllerURL,
    const char *pszXBRMSURL,
    int x,
    int y,
    const char *pszInputFilename,
    enum InputType InputType,
    long long start,
    long long end,
    struct Reader **ppReader)
{
    int err = 0;
    struct Reader *pReader = NULL;

    pReader = calloc(1, sizeof(struct Reader));
    if (!pReader)
    {
        err = ENOMEM;
        goto cleanup;
    }

    pReader->pszName = strdup(pszName);
    if (!pReader->pszName)
    {
        err = ENOMEM;
        goto cleanup;
    }

    if (pszControllerURL)
    {
        pReader->pszControllerURL = strdup(pszControllerURL);
        if (!pReader->pszControllerURL)
        {
            err = ENOMEM;
            goto cleanup;
        }
    }

    if (pszXBRMSURL)
    {
        pReader->pszXBRMSURL = strdup(pszXBRMSURL);
        if (!pReader->pszXBRMSURL)
        {
            err = ENOMEM;
            goto cleanup;
        }
    }

    pReader->uPort = uPort;
    pReader->deviceType = deviceType;
    pReader->x = x;
    pReader->y = y;
    if (pszMac)
        pReader->pszMac = strdup(pszMac);
    else
        pReader->pszMac = strdup("00:00:00:00:00:00");

    if (pszHardwareAddress)
        pReader->pszHardwareAddress = strdup(pszHardwareAddress);

    if (pszHardwareType)
        pReader->pszHardwareType = strdup(pszHardwareType);

    if (OpenSend(&pReader->curl))
    {
        err = ENOMEM;
        goto cleanup;
    }

    // All radios on by default -- I assume
    pReader->RadioPower[0] = 1;
    pReader->RadioPower[1] = 1;
    pReader->RadioPower[2] = 1;
    pReader->RadioPower[3] = 1;

    CreateQueue(&pReader->pQueue);
    CreateEventPostThread(pReader);
    CreateWebServer(pReader);

    pReader->pInputStream = NULL;
    if (pszInputFilename)
        CreateInputStream(pReader->pszName, SubmitEvent, pReader, pszInputFilename, InputType, start, end, &pReader->pInputStream);

    *ppReader = pReader;
    pReader = NULL;

cleanup:
    if (pReader)
    {
        DeleteReader(pReader);
    }
    return err;
}

int StartReader(struct Reader *pReader, struct timeval *tvNow)
{
    int err = 0;

    err = EventPostResume(pReader);
    if (pReader->pInputStream)
        err = InputStreamSignalResume(pReader->pInputStream, tvNow);

    return err;
}

int StopReader(struct Reader *pReader)
{
    int err = 0;

    if (pReader->pInputStream)
        err = InputStreamSignalPause(pReader->pInputStream);
    err = EventPostPause(pReader);

    return err;
}

int DeleteReader(struct Reader *pReader)
{
    int err = 0;
    if (pReader)
    {
        if (pReader->pInputStream)
            FreeInputStream(pReader->pInputStream);
        FreeWebServer(pReader);
        FreeEventPostThread(pReader);
        FreeQueue(pReader->pQueue);
        free(pReader->pszControllerURL);
        free(pReader->pszXBRMSURL);
        free(pReader->pszHardwareType);
        free(pReader->pszMac);
        free(pReader->pszName);
        if (pReader->curl)
        {
            CloseSend(pReader->curl);
            pReader->curl = NULL;
        }
        free(pReader);
    }
    return err;
}

static char *
GetAttribute(xmlNode *pnode, char *pszName)
{
    char *pszValue = NULL;
    char *psz = NULL;
    if (xmlHasProp(pnode, (xmlChar *) pszName) != NULL)
    {
        psz = (char *) xmlGetProp(pnode, (xmlChar *) pszName);
        pszValue = strdup(psz);
    }
    xmlFree(psz);
    return pszValue;
}

static int
GetIntAttribute(xmlNode *pnode, char *pszName, int *pVal)
{
    char *pszValue = GetAttribute(pnode, pszName);

    if (!pszValue)
        return 1;

    *pVal = atoi(pszValue);
    free(pszValue);
    return 0;
}

static int
GetLongLongAttribute(xmlNode *pnode, char *pszName, long long *pVal)
{
    char *pszValue = GetAttribute(pnode, pszName);

    if (!pszValue)
        return 1;

    *pVal = atoll(pszValue);
    free(pszValue);
    return 0;
}

static int
GetLongAttribute(xmlNode *pnode, char *pszName, long *pVal)
{
    char *pszValue = GetAttribute(pnode, pszName);

    if (!pszValue)
        return 1;

    *pVal = atol(pszValue);
    free(pszValue);
    return 0;
}

static int
ProcessControllerElement(
    xmlNode *pnode,
    struct Options *pOptions,
    struct Readers *pstReaders)
{
    if (pstReaders->pszControllerURL != NULL)
        free(pstReaders->pszControllerURL);

    if (pstReaders->pszControllerName)
        free(pstReaders->pszControllerName);

    pstReaders->pszControllerName = GetAttribute(pnode, "name");
    pstReaders->pszControllerURL = GetAttribute(pnode, "url");
    return 0;
}

static int
ProcessXBRMSElement(
    xmlNode *pnode,
    struct Options *pOptions,
    struct Readers *pstReaders)
{

    if (pstReaders->pszXBRMSURL != NULL)
        free(pstReaders->pszXBRMSURL);

    pstReaders->pszXBRMSURL = GetAttribute(pnode, "url");
    return 0;
}

static int
ProcessReaderElement(
    xmlNode *pc,
    struct Options *pOptions,
    struct Readers *pstReaders,
    struct Reader **ppReader)
{
    int err = 0;
    char *pszName = NULL;
    char *pszType = NULL;
    char *pszMac = NULL;
    char *pszHardwareType = NULL;
    char *pszHardwareAddress = NULL;
    char *pszInputFile = NULL;
    char *pszInputType = NULL;
    enum InputType InputType = InputTypeLegacyPing;
    int Port = 0;
    int x = 0;
    int y = 0;
    struct Reader *pReader = NULL;

    pszName = GetAttribute(pc, "name");
    if (!pszName)
    {
        fprintf(stderr, "Reader: Missing attribute 'name'\n");
        err = EINVAL;
        goto error;
    }

    if (pOptions->pszReaderName && strcmp(pszName, pOptions->pszReaderName))
    {
        goto cleanup;
    }

    GetIntAttribute(pc, "webport", &Port);
    GetIntAttribute(pc, "x", &x);
    GetIntAttribute(pc, "y", &y);
    pszType = GetAttribute(pc, "type");
    pszMac = GetAttribute(pc, "mac");
    pszHardwareType = GetAttribute(pc, "hwtype");
    pszHardwareAddress = GetAttribute(pc, "hardware");
    pszInputFile = GetAttribute(pc, "file");
    pszInputType = GetAttribute(pc, "fileformat");

    enum DeviceType deviceType= DeviceTypeXBR;
    if (!strcmp(pszType, "tap"))
        deviceType = DeviceTypeXTP;
    else if (!strcmp(pszType, "car"))
        deviceType = DeviceTypeCar;
    else if (!strcmp(pszType, "xtpra"))
        deviceType = DeviceTypeXtpGpio;

    if (pszInputType && !strcmp(pszInputType, "json"))
    {
        if (deviceType == DeviceTypeXBR)
            InputType = InputTypeXBR;
        else if (deviceType == DeviceTypeXTP)
            InputType = InputTypeXTP;
        else if (deviceType == DeviceTypeCar)
            InputType = InputTypeCar;
        else if (deviceType == DeviceTypeXtpGpio)
            InputType = InputTypeXtpGpio;
    }
    else if (!pszInputType || !strcmp(pszInputType, "legacy"))
    {
        if (deviceType == DeviceTypeXBR)
             InputType = InputTypeLegacyPing;
        else if (deviceType == DeviceTypeXTP)
            InputType = InputTypeLegacyTap;
    }


    if (pszType==NULL)
    {
        fprintf(stderr, "Reader %s: Element 'reader' has missing attribute(s)\n", pszName ? pszName : "(undefined)");
        err = EINVAL;
        goto error;
    }

    err = CreateReader(pszName, deviceType, pszHardwareAddress, pszHardwareType, Port, pszMac, pstReaders->pszControllerURL, pstReaders->pszXBRMSURL, x, y, pszInputFile, InputType, pstReaders->start, pstReaders->end, &pReader);
    if (err)
    {
        goto error;
    }
    *ppReader = pReader;
    pReader = NULL;

cleanup:
    free(pszName);
    free(pszType);
    free(pszMac);
    free(pszHardwareType);
    free(pszHardwareAddress);
    free(pszInputFile);
    free(pszInputType);

    return err;

error:
    goto cleanup;
}

static int
ProcessReadersElement(
    xmlNode *pnode,
    struct Options *pOptions,
    struct Readers *pstReaders,
    struct Reader ***pppReaders,
    size_t *psReaders)
{
    int err = 0;

    xmlNode *pc;
    struct Reader **ppReaders = NULL;
    size_t sReaders = 0;
    size_t i;


    GetLongLongAttribute(pnode, "start", &pstReaders->start);
    GetLongLongAttribute(pnode, "end", &pstReaders->end);

    for (pc = pnode->children; pc != NULL; pc = pc->next)
    {
        struct Reader *pReader = NULL;
        if (pc->type == XML_ELEMENT_NODE)
        {
            if (strcmp((char *)pc->name, "reader")!=0)
            {
                fprintf(stderr, "Reader: Unexpected element %s\n", (char *)pc->name);
                err = EINVAL;
                goto cleanup;
            }

            err = ProcessReaderElement(pc, pOptions, pstReaders, &pReader);
            if (err)
            {
                goto error;
            }

            if (pReader)
            {
                size_t sNewReaders = sReaders + 1;
                struct Reader **ppNewReaders = malloc(sizeof(struct Reader*) * sNewReaders);
                if (!ppNewReaders)
                {
                    err = ENOMEM;
                    goto error;
                }
                if (ppReaders)
                {
                    memcpy(ppNewReaders, ppReaders, sReaders * sizeof(struct Reader*));
                    free(ppReaders);
                }
                ppReaders = ppNewReaders;
                ppReaders[sReaders] = pReader;
                sReaders = sNewReaders;
            }
        }
    }

    // if don't have the one we're looking for, it's a problem
    if (pOptions->pszReaderName != NULL && sReaders == 0)
    {
        fprintf(stderr, "Reader: Could not find reader %s in the configuration file.", pOptions->pszReaderName);
        err = ESRCH;
        goto cleanup;
    }

    *pppReaders = ppReaders;
    *psReaders = sReaders;

cleanup:
    return err;

error:
    for (i = 0; i < sReaders; i++)
    {
        DeleteReader(ppReaders[i]);
    }
    free(ppReaders);
    ppReaders = NULL;
    sReaders = 0;
    goto cleanup;
}

/**
 * ProcessConfigurationElements:
 * @a_node: the initial xml node to consider.
 *
 */
static int
ProcessConfigurationElements(xmlNode *pNodeHead, struct Options *pOptions, struct Reader ***pppReaders, size_t *psReaders)
{
    int err = 0;
    struct Readers stReaders;
    xmlNode *pNode = NULL;

    stReaders.pszControllerName = NULL;
    stReaders.pszControllerURL = NULL;
    stReaders.pszXBRMSURL = NULL;
    stReaders.start = 0;
    stReaders.end = -1;

    for (pNode=pNodeHead; pNode!=NULL; pNode = pNode->next)
    {
        if (pNode->type == XML_ELEMENT_NODE)
        {
            char *pszName = (char *) pNode->name;

            if (strcmp(pszName, "readers")==0)
                err = ProcessReadersElement(pNode, pOptions, &stReaders, pppReaders, psReaders);
            else if (strcmp(pszName, "controller")==0)
                err = ProcessControllerElement(pNode, pOptions, &stReaders);
            else if (strcmp(pszName, "xbrms")==0)
                err = ProcessXBRMSElement(pNode, pOptions, &stReaders);

            else
            {
                fprintf(stderr, "Unexpected element: %s\n", pszName);
            }

            if (err != 0)
                goto cleanup;
        }
    }

cleanup:
    free(stReaders.pszControllerName);
    free(stReaders.pszControllerURL);
    return err;
}


int
CreateReadersFromConfiguration(
    struct Options *pOptions,
    struct Reader ***pppReaders,
    size_t *psReaders)
{
    int err = 0;

    xmlDoc *doc = NULL;
    xmlNode *root = NULL;

    /*
     * this initialize the library and check potential ABI mismatches
     * between the version it was compiled for and the actual shared
     * library used.
     */
    LIBXML_TEST_VERSION

    /*parse the file and get the DOM */
    doc = xmlReadFile(pOptions->pszConfigFile, NULL, 0);

    if (doc == NULL)
    {
        fprintf(stderr, "error: could not parse file %s\n", pOptions->pszConfigFile);
        err = ENOMEM;
        goto cleanup;
    }

    /* Get the root element node */
    root = xmlDocGetRootElement(doc);

    // verify it's t;he "configuration" element
    if (strcmp((char *)root->name, "configuration")!=0)
    {
        fprintf(stderr, "Expected configuration element, got %s\n", root->name);
        err = ENOMEM;
        goto cleanup;
    }

    err = ProcessConfigurationElements(root->children, pOptions, pppReaders, psReaders);

cleanup:

    if (doc)
    {
        /*free the document */
        xmlFreeDoc(doc);
    }

    return err;
}

const char *ReaderName(struct Reader *pReader)
{
    return pReader->pszName;
}

enum DeviceType ReaderType(struct Reader *pReader)
{
    return pReader->deviceType;
}

uint16_t ReaderPort(struct Reader *pReader)
{
    return pReader->uPort;
}

void ReaderStats(
    struct Reader *pReader,
    int *bFailedLastSend,
    unsigned long long *goodSendCount,
    unsigned long long *badSendCount,
    int *bFailedLastHello)
{
    *bFailedLastSend = pReader->bFailedLastSend;
    *goodSendCount = pReader->goodSendCount;
    *badSendCount = pReader->badSendCount;
    *bFailedLastHello = pReader->bFailedLastHello;
}

const char* GetReaderType(enum DeviceType deviceType)
{
    static const char* types[] = {
            "Unknown",
            "Long Range",
            "xFP",
            "AVMS Vehicle",
            "xTPRA"
    };

    return types[deviceType];
}

