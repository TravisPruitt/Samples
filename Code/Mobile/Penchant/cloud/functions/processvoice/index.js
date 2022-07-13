/**
 * Triggered from a change to a Cloud Storage bucket.
 *
 * @param {!Object} event Event payload.
 * @param {!Object} context Metadata for the event.
 */

require('@google-cloud/debug-agent').start({ allowExpressions: true });

//const vision = require("@google-cloud/vision");
const { Storage } = require("@google-cloud/storage");
const speech = require("@google-cloud/speech");
const Sentiment = require("@google-cloud/language");
const Firestore = require('@google-cloud/firestore');
const path = require('path');

const storage = new Storage();
const client = new speech.SpeechClient();
const sentimentClient = new Sentiment.LanguageServiceClient();

const PROJECTID = 'penchant-b3980';
const firestore = new Firestore({
    projectId: PROJECTID,
    timestampsInSnapshots: true,
});

exports.testprocessvoice = (req, res) => {
    console.log("Hello Google Cloud Functions Emulator");
    res.send("Success");
};

exports.processvoice = async (event) => {
    const object = event;
    const uploadedpath = `gs://${object.bucket}/${object.name}`;
    console.log(`Analyzing ${uploadedpath}.`);

    // If this is triggered due to the json file we are auto gen-ing, don't even
    // bother logging it.
    if (object.name.includes(".json")) {
        return;
    }

    // But if it is triggere by a file type we don't support that a user uploaded
    // we should log it.
    if (!isSupported(object.name)) {
        console.log(`Filetype not supported ${uploadedpath}`);
        return;
    }

    const documentId = path.basename(object.name, path.extname(object.name));
    const filename = object.name.split(".")[0];
    const ext = object.name.split(".").pop();
    const newname = object.name.replace(ext, "json");
    const sentimentFilename = filename + "_s.json";

    const audio = {
        uri: uploadedpath,
    };

    const config = {
        encoding: 'FLAC',
        sampleRateHertz: 16000,
        languageCode: 'en-US',
        enableWordTimeOffsets: true,

    };

    const request = {
        audio,
        config,
    };

    const [operation] = await client.longRunningRecognize(request);
    // Get a Promise representation of the final results of the job
    const [response] = await operation.promise();
    console.log("Speech results: " + JSON.stringify(response.results));

    try {
        const bucket = storage.bucket(object.bucket);
        const gcFile = bucket.file(newname);

        gcFile.save(JSON.stringify(response.results), function (err) {
            if (!err) {
                console.error(`File written successfull ${newname}.`);
            }
        })

        // Write the raw text file.
        // Write the output of sentiment analysis.
        const transcription = response.results
            .map(result => result.alternatives[0].transcript)
            .join('. ');

        const rawdocument = {
            type: 'PLAIN_TEXT',
            content: transcription,
        };
        console.log('Sending raw text to sentiment request: ' + JSON.stringify(rawdocument));

        const [sOperation] = await sentimentClient.analyzeEntitySentiment({ document: rawdocument });
        // Get a Promise representation of the final results of the job
        //const sentiment = sOperation.documentSentiment;
        console.log(`Sentiment results: ` + JSON.stringify(sOperation));

        // Write the output of sentiment analysis.
        const sgcFile = bucket.file(sentimentFilename);

        sgcFile.save(JSON.stringify(sOperation), function (err) {
            if (!err) {
                console.error(`File written successfull ${sentimentFilename}.`);
            }
        })

        // Update the tags, ensuring to save any existing tags.
        const foundTags = findTags(sOperation);
        // const existingTags = await firestore.collection("messages").doc(documentId)['tags'];
        // foundTags.forEach(tag => {
        //     existingTags.push(tag);
        // });

        // Write the transcription results and sentiment analysis.
        console.log(`Updating database using documentId: ${documentId}`);
        await firestore.collection("messages").doc(documentId).update({ 'text': transcription, 'translation': JSON.stringify(response.results), 'tags': Firestore.FieldValue.arrayUnion(...foundTags), 'sentiment': JSON.stringify(sOperation) });
        console.log(`Finished updating DB.`);
    }
    catch (err) {
        console.error(`Unable to write results file: ${newname}.`, err);
        throw err;
    }

    return;
}

function findTags(sentiment) {
    var tags = [];

    sentiment.entities.forEach(entity => {
        if (entity.salience >= .25) {
            tags.push(entity.name.toLowerCase());
        }
    });

    return tags;
}

function isSupported(filename) {
    const extensions = [".flac", ".wav"];
    return extensions.some(function (entry) {
        if (filename.includes(entry)) {
            return true;
        }
        return false;
    });
}
