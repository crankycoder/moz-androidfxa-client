// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/
// package com.example.sarentz.firefoxaccounts;

package com.example.sarentz.firefoxaccounts;

public class AuthorizationResult
{
    private final String email;
    private final String accessToken;

    public AuthorizationResult(String email, String accessToken) {
        this.email = email;
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
