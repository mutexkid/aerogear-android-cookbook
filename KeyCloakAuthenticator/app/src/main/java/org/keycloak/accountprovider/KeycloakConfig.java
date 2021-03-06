/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.accountprovider;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.accountprovider.util.IOUtils;
import org.keycloak.accountprovider.util.ObjectUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class KeycloakConfig {

    private static final String TAG = KeycloakConfig.class.getSimpleName().toUpperCase();

    public final String realmUrl;
    public final String authServerUrl;
    public final String realm;
    public final String clientId;
    public final String clientSecret;

    private static KeycloakConfig instace;

    private KeycloakConfig(Context context) {
        InputStream fileStream = context.getResources().openRawResource(R.raw.keycloak);
        String configText = IOUtils.getString(fileStream);
        IOUtils.close(fileStream);

        try {
            JSONObject configFile = new JSONObject(configText);
            realm = configFile.getString("realm");
            authServerUrl = configFile.getString("auth-server-url");
            realmUrl = authServerUrl + "/realms/" + URLEncoder.encode(realm, "UTF-8");
            clientId = configFile.getString("resource");
            clientSecret = ObjectUtils.getOrDefault(configFile.optJSONObject("credentials"), new JSONObject()).optString("secret");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("You don't support UTF-8", e);
        }
    }

    public static synchronized KeycloakConfig getInstance(Context context) {
        if (instace == null) {
            instace = new KeycloakConfig(context);
        }
        return  instace;
    }



}
