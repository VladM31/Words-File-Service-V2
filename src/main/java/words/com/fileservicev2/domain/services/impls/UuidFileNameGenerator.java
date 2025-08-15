package words.com.fileservicev2.domain.services.impls;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import words.com.fileservicev2.domain.services.FileNameGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Builder
@RequiredArgsConstructor
class UuidFileNameGenerator implements FileNameGenerator {
    private final ZoneId zoneId = ZoneId.of("UTC");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_").withZone(zoneId);
    private final RandomGenerator ran;

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();

        builder.append(LocalDateTime.now(zoneId).format(dateTimeFormatter));
        builder.append(UUID.randomUUID().toString().replaceAll("-", ""));


        ran.ints()
                .limit(Math.round(builder.length() * 35.0 / 100.0))
                .map(i -> Math.abs(i % builder.length()))
                .distinct()
                .forEach(i -> builder.setCharAt(i, Character.toUpperCase(builder.charAt(i))));


        return builder.toString();
    }

}
