package de.mediathekview.mserver.crawler.ndr.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import de.mediathekview.mserver.crawler.basic.CrawlerUrlDTO;
import de.mediathekview.mserver.crawler.rbb.parser.RbbFilmInfoDto;
import de.mediathekview.mserver.testhelper.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NdrFilmDetailDeserializerTest {

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][]{
            {
                "https://www.ndr.de/fernsehen/sendungen/sass-so-isst-der-norden/Sass-So-isst-der-Norden,sendung563148.html",
                "/ndr/ndr_film_detail1.html",
                "https://www.ndr.de/fernsehen/sendungen/sass-so-isst-der-norden/sass404-ardjson_image-5a3e2524-70e1-45f1-96da-27bf9d5c8137.json",
                "Sass: So isst der Norden",
                "Deftige Eintopfgerichte aus Bremen",
                "Rainer Sass macht mit seiner mobilen Küche Station auf dem Bremer Domshof. Mit seinen Kochpartnern vom dortigen Markt bereitet er Deftiges aus frischem Gemüse zu.",
                LocalDateTime.of(2018, 3, 18, 16, 30, 0),
                Duration.ofMinutes(30).plusSeconds(6)
            },
            {
                "https://www.ndr.de/fernsehen/Folge-2881-Der-langersehnte-Antrag,sturmderliebe1816.html",
                "/ndr/ndr_film_detail2.html",
                "https://www.ndr.de/fernsehen/sturmderliebe1816-ardjson_image-a5409105-e38b-4847-ba32-fc2c337d7515.json",
                "Sturm der Liebe",
                "Folge 2881: Der langersehnte Antrag",
                "André fasst sich ein Herz und macht Melli den geplanten Antrag. Währenddessen ist Romy enttäuscht, dass Paul lediglich eine gute Freundin in ihr sieht.",
                LocalDateTime.of(2018, 3, 15, 8, 10, 0),
                Duration.ofMinutes(48).plusSeconds(34)
            },
            {
                "https://www.ndr.de/fernsehen/sendungen/tatort/Blutschuld,sendung751232.html",
                "/ndr/ndr_film_detail_m3u8.html",
                "https://www.ndr.de/fernsehen/livestream/livestream217-ardjson_image-5e9560f0-bc96-4d5b-8a92-cddd3f77966f.json",
                "Tatort",
                "Blutschuld",
                "Der Abfallunternehmer Harald Kosen ist in seinem Schlafzimmer erschlagen worden. Die Kommissare Saalfeld und Keppler sind geschockt von dem kaltblütigen Vorgehen des Mörders.",
                LocalDateTime.of(2018, 3, 20, 22, 0, 0),
                Duration.ofMinutes(90)
            }
        });
  }

  private final String requestUrl;
  private final String htmlPage;
  private final String expectedVideoUrl;
  private final String expectedTopic;
  private final String expectedTitle;
  private final String expectedDescription;
  private final LocalDateTime expectedTime;
  private final Duration expectedDuration;

  public NdrFilmDetailDeserializerTest(final String aRequestUrl, final String aHtmlPage, final String aExpectedVideoUrl,
      final String aExpectedTopic, final String aExpectedTitle, final String aExpectedDescription, final LocalDateTime aExpectedTime,
      final Duration aExpectedDuration) {

    requestUrl = aRequestUrl;
    htmlPage = aHtmlPage;
    expectedVideoUrl = aExpectedVideoUrl;
    expectedTopic = aExpectedTopic;
    expectedTitle = aExpectedTitle;
    expectedDescription = aExpectedDescription;
    expectedTime = aExpectedTime;
    expectedDuration = aExpectedDuration;
  }

  @Test
  public void deserializeTest() {

    final String htmlContent = FileReader.readFile(htmlPage);
    final Document document = Jsoup.parse(htmlContent);

    final NdrFilmDeserializer target = new NdrFilmDeserializer();
    final Optional<RbbFilmInfoDto> actual = target.deserialize(new CrawlerUrlDTO(requestUrl), document);

    assertThat(actual.isPresent(), equalTo(true));
    RbbFilmInfoDto dto = actual.get();

    assertThat(dto.getTopic(), equalTo(expectedTopic));
    assertThat(dto.getTitle(), equalTo(expectedTitle));
    assertThat(dto.getDescription(), equalTo(expectedDescription));
    assertThat(dto.getWebsite(), equalTo(requestUrl));
    assertThat(dto.getDuration(), equalTo(expectedDuration));
    assertThat(dto.getTime(), equalTo(expectedTime));
    assertThat(dto.getUrl(), equalTo(expectedVideoUrl));
  }
}
