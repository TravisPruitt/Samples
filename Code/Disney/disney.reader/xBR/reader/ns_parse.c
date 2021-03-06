/**
 *  @file   ns_lookup.c
 *  @date   Sep, 2012
 *
 *  This file is adapted from the glibc source file with the same name
 *  and provides implementation of functions not exported by the resolv
 *  library. It's copyright and license information are reproduced below.
 */

/*
 * Copyright (c) 2004 by Internet Systems Consortium, Inc. ("ISC")
 * Copyright (c) 1996,1999 by Internet Software Consortium.
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND ISC DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS.  IN NO EVENT SHALL ISC BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT
 * OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


#include <sys/types.h>

#include <netinet/in.h>
#include <arpa/nameser.h>

#include <errno.h>
#include <resolv.h>
#include <string.h>


static void setsection(ns_msg *msg, ns_sect sect);


#define RETERR(err) do { return (-1); } while (0)


int ns_skiprr(const u_char *ptr, const u_char *eom, ns_sect section, int count)
{
    const u_char *optr = ptr;

    for ((void)NULL; count > 0; count--) {
        int b, rdlength;

        b = dn_skipname(ptr, eom);
        if (b < 0)
            RETERR(EMSGSIZE);
        ptr += b/*Name*/ + NS_INT16SZ/*Type*/ + NS_INT16SZ/*Class*/;
        if (section != ns_s_qd) {
            if (ptr + NS_INT32SZ + NS_INT16SZ > eom)
                RETERR(EMSGSIZE);
            ptr += NS_INT32SZ/*TTL*/;
            NS_GET16(rdlength, ptr);
            ptr += rdlength/*RData*/;
        }
    }
    if (ptr > eom)
        RETERR(EMSGSIZE);
    return (ptr - optr);
}


int ns_initparse(const u_char *msg, int msglen, ns_msg *handle)
{
    const u_char *eom = msg + msglen;
    int i;

    memset(handle, 0x5e, sizeof *handle);
    handle->_msg = msg;
    handle->_eom = eom;
    if (msg + NS_INT16SZ > eom)
        RETERR(EMSGSIZE);
    NS_GET16(handle->_id, msg);
    if (msg + NS_INT16SZ > eom)
        RETERR(EMSGSIZE);
    NS_GET16(handle->_flags, msg);
    for (i = 0; i < ns_s_max; i++) {
        if (msg + NS_INT16SZ > eom)
            RETERR(EMSGSIZE);
        NS_GET16(handle->_counts[i], msg);
    }
    for (i = 0; i < ns_s_max; i++)
        if (handle->_counts[i] == 0)
            handle->_sections[i] = NULL;
        else {
            int b = ns_skiprr(msg, eom, (ns_sect)i,
                              handle->_counts[i]);

            if (b < 0)
                return (-1);
            handle->_sections[i] = msg;
            msg += b;
        }
    if (msg != eom)
        RETERR(EMSGSIZE);
    setsection(handle, ns_s_max);
    return (0);
}


int ns_parserr(ns_msg *handle, ns_sect section, int rrnum, ns_rr *rr)
{
    int b;
    int tmp;

    /* Make section right. */
    tmp = section;
    if (tmp < 0 || section >= ns_s_max)
        RETERR(ENODEV);
    if (section != handle->_sect)
        setsection(handle, section);

    /* Make rrnum right. */
    if (rrnum == -1)
        rrnum = handle->_rrnum;
    if (rrnum < 0 || rrnum >= handle->_counts[(int)section])
        RETERR(ENODEV);
    if (rrnum < handle->_rrnum)
        setsection(handle, section);
    if (rrnum > handle->_rrnum) {
        b = ns_skiprr(handle->_ptr, handle->_eom, section,
                      rrnum - handle->_rrnum);

        if (b < 0)
            return (-1);
        handle->_ptr += b;
        handle->_rrnum = rrnum;
    }

    /* Do the parse. */
    b = dn_expand(handle->_msg, handle->_eom,
                  handle->_ptr, rr->name, NS_MAXDNAME);
    if (b < 0)
        return (-1);
    handle->_ptr += b;
    if (handle->_ptr + NS_INT16SZ + NS_INT16SZ > handle->_eom)
        RETERR(EMSGSIZE);
    NS_GET16(rr->type, handle->_ptr);
    NS_GET16(rr->rr_class, handle->_ptr);
    if (section == ns_s_qd) {
        rr->ttl = 0;
        rr->rdlength = 0;
        rr->rdata = NULL;
    } else {
        if (handle->_ptr + NS_INT32SZ + NS_INT16SZ > handle->_eom)
            RETERR(EMSGSIZE);
        NS_GET32(rr->ttl, handle->_ptr);
        NS_GET16(rr->rdlength, handle->_ptr);
        if (handle->_ptr + rr->rdlength > handle->_eom)
            RETERR(EMSGSIZE);
        rr->rdata = handle->_ptr;
        handle->_ptr += rr->rdlength;
    }
    if (++handle->_rrnum > handle->_counts[(int)section])
        setsection(handle, (ns_sect)((int)section + 1));

    /* All done. */
    return (0);
}


static void setsection(ns_msg *msg, ns_sect sect)
{
    msg->_sect = sect;
    if (sect == ns_s_max) {
        msg->_rrnum = -1;
        msg->_ptr = NULL;
    } else {
        msg->_rrnum = 0;
        msg->_ptr = msg->_sections[(int)sect];
    }
}
