import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class DateUtils {
    public static Date parseDate(String date) throws ParseException {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(date);
    }

    public static Date randomBetween(Date startInclusive, Date endExclusive) {
        long startSeconds = startInclusive.getTime();
        long endSeconds = endExclusive.getTime();
        long random = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds);

        return new Date(Instant.ofEpochSecond(random).toEpochMilli());
    }
}
