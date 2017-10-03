package mServer.crawler.sender.wdr;

import de.mediathekview.mlib.Const;
import de.mediathekview.mlib.daten.DatenFilm;
import java.util.Arrays;
import java.util.Collection;
import mServer.test.HtmlFileReader;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class WdrVideoDetailsDeserializerTest {
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {  
            { "/wdr/wdr_video_details1.html", "Abenteuer Erde", "Die Tricks des Überlebens 3) Im Wald", "Nur auf der Nordhalbkugel gibt es Wälder, deren Leben durch große Veränderungen geprägt wird. Jedes Jahr lässt sich hier ein wundersamer Wechsel beobachten: im Winter sinken die Temperaturen dramatisch und die Wälder werden völlig kahl. Im Frühjahr kehren mit steigenden Temperaturen die grünen Blätter und damit das Leben zurück. Autor/-in: Paul Bradshaw", "http://www1.wdr.de/mediathek/video/sendungen/abenteuer-erde/video-die-tricks-des-ueberlebens--im-wald-102.html", "http://wdradaptiv-vh.akamaihd.net/i/medp/ondemand/weltweit/fsk0/148/1480611/,1480611_16974214,1480611_16974213,1480611_16974215,1480611_16974211,1480611_16974212,.mp4.csmil/index_2_av.m3u8", "179|0_av.m3u8", "", "", "26.09.2017", "20:15:00", "00:43:20" }
        });
    }

    private final String htmlFile;
    private final String expectedTheme;
    private final String expectedTitle;
    private final String expectedDescription;
    private final String expectedWebsite;
    private final String expectedVideoUrlSmall;
    private final String expectedVideoUrlNormal;
    private final String expectedVideoUrlHd;
    private final String expectedSubtitle;
    private final String expectedDate;
    private final String expectedTime;
    private final String expectedDuration;
    
    private final WdrVideoDetailsDeserializer target;
    
    public WdrVideoDetailsDeserializerTest(String aHtmlFile, String aTheme, String aTitle, String aDescription, String aWebsite, String aVideoUrlNormal, String aVideoUrlSmall, String aVideoUrlHd, String aSubtitle, String aDate, String aTime, String aDuration) {
        htmlFile = aHtmlFile;
        expectedDate = aDate;
        expectedDescription = aDescription;
        expectedDuration = aDuration;
        expectedTheme = aTheme;
        expectedTime = aTime;
        expectedTitle = aTitle;
        expectedSubtitle = aSubtitle;
        expectedVideoUrlSmall = aVideoUrlSmall;
        expectedVideoUrlNormal = aVideoUrlNormal;
        expectedVideoUrlHd = aVideoUrlHd;
        expectedWebsite = aWebsite;
        
        target = new WdrVideoDetailsDeserializer();
    }
    
    @Test
    public void deserializeTestWithVideo() {
        String html = HtmlFileReader.readHtmlPage(htmlFile);
        Document document = Jsoup.parse(html);
        
        DatenFilm actual = target.deserialize(expectedTheme, document);
        
        assertThat(actual, notNullValue());
        assertThat(actual.arr[DatenFilm.FILM_SENDER], equalTo(Const.WDR));
        assertThat(actual.arr[DatenFilm.FILM_THEMA], equalTo(expectedTheme));
        assertThat(actual.arr[DatenFilm.FILM_TITEL], equalTo(expectedTitle));
        assertThat(actual.arr[DatenFilm.FILM_BESCHREIBUNG], equalTo(expectedDescription));
        assertThat(actual.arr[DatenFilm.FILM_WEBSEITE], equalTo(expectedWebsite));
        assertThat(actual.arr[DatenFilm.FILM_DATUM], equalTo(expectedDate));
        assertThat(actual.arr[DatenFilm.FILM_ZEIT], equalTo(expectedTime));
        assertThat(actual.arr[DatenFilm.FILM_DAUER], equalTo(expectedDuration));
        assertThat(actual.getUrl(), equalTo(expectedVideoUrlNormal));
        assertThat(actual.arr[DatenFilm.FILM_URL_KLEIN], equalTo(expectedVideoUrlSmall));
        assertThat(actual.arr[DatenFilm.FILM_URL_HD], equalTo(expectedVideoUrlHd));
        assertThat(actual.getUrlSubtitle(), equalTo(expectedSubtitle));
    }
}
