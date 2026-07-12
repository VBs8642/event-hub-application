package com.event_hub.event_hub.mapper.registration;
import com.event_hub.event_hub.model.dto.registration.RegistrationDto;
import com.event_hub.event_hub.model.entity.registration.Registration;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RegistrationMapper {
    public static RegistrationDto toDto(Registration registration) {
        if (registration == null) {
            return null;
        }

        return RegistrationDto.builder()
                .eventId(registration.getId())
                .attendeesCount(registration.getAttendeesCount())
                .registrationDate(registration.getRegistrationDate())
                .build();
    }
}
