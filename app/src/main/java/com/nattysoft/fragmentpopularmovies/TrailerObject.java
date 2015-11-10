package com.nattysoft.fragmentpopularmovies;

/**
 * Created by F3838284 on 2015/11/10.
 */
public class TrailerObject {
    String site = null;
    String trailerID = null;
    String iso_639_1 = null;
    String trailerType = null;
    String trailerKey = null;
    String trailerSize = null;
    String trailerName = null;

    public TrailerObject(String site, String trailerID, String iso_639_1, String trailerType, String trailerKey, String trailerSize, String trailerName){
        this.site = site;

        this.trailerID = trailerID;

        this.iso_639_1 = iso_639_1;

        this.trailerType = trailerType;

        this.trailerKey = trailerKey;

        this.trailerSize = trailerSize;

        this.trailerName = trailerName;

    }
}
