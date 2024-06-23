package no.persistence.jiraworklog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimelisteFileFormatImplRepoTest {

    @Test
    void getByName() {
        assertEquals(TimelisteFileFormatDefaultImpl.STANDARD, TimelisteFileFormatImplRepo.getByName(TimelisteFileFormatDefaultImpl.STANDARD).getFormatName());
    }
}