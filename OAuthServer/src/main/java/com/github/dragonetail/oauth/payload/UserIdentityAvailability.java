package com.github.dragonetail.oauth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserIdentityAvailability {
    private Boolean available;
}
