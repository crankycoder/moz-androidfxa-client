// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/
// package com.example.sarentz.firefoxaccounts;

package com.example.sarentz.firefoxaccounts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity
{
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        updateMenuTitle();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_login) {
            if (isLoggedIn()) {
                logout();
                updateMenuTitle();
                updateText();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                updateMenuTitle();
                updateText();
            }
        }
    }

    boolean isLoggedIn() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MySettings", 0);
        return preferences.getString("email", null) != null;
    }

    void updateText() {
        final TextView textView = (TextView) findViewById(R.id.textView);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MySettings", 0);
        String email = preferences.getString("email", null);
        if (email != null) {
            textView.setText("Hello, " + email);
        } else {
            textView.setText("Not logged in");
        }
    }

    void updateMenuTitle() {
        MenuItem menuItem = menu.findItem(R.id.action_login);
        if (isLoggedIn()) {
            menuItem.setTitle("Logout");
        } else {
            menuItem.setTitle("Login");
        }
    }

    void logout() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MySettings", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("email");
        editor.remove("accessToken");
        editor.apply();
    }

}
