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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUserInterface();

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("MySettings", 0);
                String accessToken = preferences.getString("accessToken", null);

                ApiTask task = new ApiTask(accessToken, MainActivity.this);
                task.execute("Android");
            }
        });
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
                updateUserInterface();
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
                updateUserInterface();
            }
        }
    }

    void showApiResponse(ApiResponse response) {
        Toast toast = Toast.makeText(this, "Server said: \"" + response.getMessage() + "\"", Toast.LENGTH_SHORT);
        toast.show();
    }

    boolean isLoggedIn() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MySettings", 0);
        return preferences.getString("email", null) != null;
    }

    void updateUserInterface() {
        final TextView textView = (TextView) findViewById(R.id.textView);
        final Button button = (Button) findViewById(R.id.button);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MySettings", 0);
        String email = preferences.getString("email", null);
        if (email != null) {
            textView.setText("Hello, " + email);
            button.setVisibility(View.VISIBLE);
        } else {
            textView.setText("Not logged in");
            button.setVisibility(View.INVISIBLE);
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
