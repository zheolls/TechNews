package hit.TechNews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = getIntent().getIntExtra("theme", R.style.DayLightTheme);
        setTheme(theme);

        setContentView(R.layout.activity_about);

    }
}
