package com.disney.xband.xbrms.common.model;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/14/13
 * Time: 12:57 PM
 */
public class Comparators {

    public static class HealthStatusComparator implements Comparator<HealthStatusDto> {

        public int compare(HealthStatusDto hs1, HealthStatusDto hs2){

            return StatusType.compare(hs1.getStatus(), hs2.getStatus());
        }
    }

    /**
     * Use to sort a collection of <code>XbrcDto</code> objects by their
     * venue/facility name in lexicographical order.
     *
     * @author Iwona Glabek
     */
    public static class VenueComparator implements Comparator<XbrcDto> {
        public int compare(XbrcDto xbrcDto1, XbrcDto xbrcDto2) {

            String xbrc1VenueId = xbrcDto1.getFacilityId();
            String xbrc2VenueId = xbrcDto2.getFacilityId();

            if ((xbrc1VenueId == null) || (xbrc2VenueId == null)) {
                return -1;
            }

            if (xbrc1VenueId.compareTo(xbrc2VenueId) > 0) {
                return 1;
            }
            if (xbrc1VenueId.compareTo(xbrc2VenueId) < 0) {
                return -1;
            }

            return 0;
        }
    }

    /**
     * Use to sort a collection of <code>dLocationInfoDto</code> objects
     * by xbrcDto's venue/facility id.
     *
     * @author Iwona Glabek
     *
     */
    public static class ByVenueComparator implements Comparator<LocationInfoDto> {
		public int compare(LocationInfoDto xrd1, LocationInfoDto xrd2){

			XbrcDto xbrcDto1 = xrd1.getXbrc();
			XbrcDto xbrcDto2 = xrd2.getXbrc();

			VenueComparator venueComparator = new VenueComparator();

			return venueComparator.compare(xbrcDto1, xbrcDto2);
		}
	}

    /**
     * Use to sort a collection of <code>XbrcDto</code> objects by their high availability
     * status (HA). This comparator uses <code>HAStatusEnum.compare()</code> method.
     *
     * @author Iwona Glabek
     */
    public static class HAStatusComparator implements Comparator<XbrcDto> {
        public int compare(XbrcDto xbrcDto1, XbrcDto xbrcDto2) {

            HAStatusEnum xbrc1HAStatus = HAStatusEnum.valueOf(xbrcDto1.getHaStatus());
            HAStatusEnum xbrc2HAStatus = HAStatusEnum.valueOf(xbrcDto2.getHaStatus());

            return HAStatusEnum.compare(xbrc1HAStatus, xbrc2HAStatus);
        }
    }

    public static class XbrcByVenueAndHAStatusComparator implements Comparator<HealthItem> {

        public int compare(HealthItem hid1, HealthItem hid2){

            HealthItemDto xbrc1 = hid1.getItem();
            HealthItemDto xbrc2 = hid2.getItem();

            if (!(xbrc1 instanceof XbrcDto) || !(xbrc2 instanceof XbrcDto))
                throw new IllegalArgumentException("XbrcByVenueDisplayComparator is only capable of comparing healthItemDisplay for Xbrc health items");

            Comparators.VenueComparator venueComparator = new Comparators.VenueComparator();

            int result = venueComparator.compare((XbrcDto)xbrc1, (XbrcDto)xbrc2);

            if (result != 0)
                return result;

            Comparators.HAStatusComparator haStatusComparator = new Comparators.HAStatusComparator();

            return haStatusComparator.compare((XbrcDto)xbrc1, (XbrcDto)xbrc2);
        }
    }

    public static class HealtItemByHAStatusComparator implements Comparator<HealthItem> {
        public int compare(HealthItem hid1, HealthItem hid2){

            HealthItemDto xbrc1 = hid1.getItem();
            HealthItemDto xbrc2 = hid2.getItem();

            Comparators.HAStatusComparator haStatusComparator = new Comparators.HAStatusComparator();

            return haStatusComparator.compare((XbrcDto)xbrc1, (XbrcDto)xbrc2);
        }
    }
}
