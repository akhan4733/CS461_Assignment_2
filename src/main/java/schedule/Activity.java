package schedule;

import java.util.ArrayList;
import java.util.List;

public record Activity(String name,
                       int expectedEnrollment,
                       List<String> preferredFacilitators,
                       List<String> otherFacilitators) {

    public Activity copy() {
        return new Activity(
                name,
                expectedEnrollment,
                new ArrayList<>(preferredFacilitators),
                new ArrayList<>(otherFacilitators)
        );
    }
}
