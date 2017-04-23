package mServer.crawler.sender;

import de.mediathekview.mlib.Const;
import mServer.crawler.FilmeSuchen;

public class MediathekArte_en extends MediathekArte_de {

    public final static String SENDERNAME = Const.ARTE_EN;

    public MediathekArte_en(FilmeSuchen ssearch, int startPrio) {
        super(ssearch, startPrio, SENDERNAME);
        URL_CONCERT = "http://concert.arte.tv/en/videos/all";
        URL_CONCERT_NOT_CONTAIN = "-STA";
        URL_ARTE_MEDIATHEK_1 = "http://www.arte.tv/guide/en/plus7/videos?day=-";
        URL_ARTE_MEDIATHEK_2 = "&page=1&isLoading=true&sort=newest&country=DE";
        TIME_1 = "Available from";
        TIME_2 = "at";
    }
}
