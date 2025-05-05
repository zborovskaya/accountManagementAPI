package by.zborovskaya.accountManagementAPI.dto;

import by.zborovskaya.accountManagementAPI.validation.ValidIdentifier;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneUpdateRequest {
    @Valid
    private List<@ValidIdentifier String> phonesToAdd = new ArrayList<>();

    @Valid
    private List<@ValidIdentifier String> phonesToRemove = new ArrayList<>();
}
