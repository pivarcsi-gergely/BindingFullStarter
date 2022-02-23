package phil.petrik.bindingfull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.IOException;

import phil.petrik.bindingfull.data.Film;
import phil.petrik.bindingfull.data.RequestTask;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        handleListeners();
        binding.buttonSync.callOnClick();
    }

    private void handleListeners() {
        binding.buttonNew.setOnClickListener(($) -> {
            binding.layoutFilmEditor.setVisibility(View.VISIBLE);
            binding.layoutFilmInspector.setVisibility(View.GONE);
        });
        binding.buttonSync.setOnClickListener(($) -> {
            setFilms();
        });
        binding.buttonClose.setOnClickListener(($) -> {
            binding.layoutFilmInspector.setVisibility(View.GONE);
        });
        binding.buttonCloseEditor.setOnClickListener(($) -> {
            binding.layoutFilmEditor.setVisibility(View.GONE);
        });
        binding.buttonAlter.setOnClickListener(($) -> {
            binding.layoutFilmEditor.setVisibility(View.VISIBLE);
            binding.layoutFilmInspector.setVisibility(View.GONE);
        });
        binding.buttonSend.setOnClickListener(($) -> {
            sendFilm(Film.emptyFilm());
        });
    }

    private void setFilm(int id) {
        try {
            binding.layoutFilmInspector.setVisibility(View.VISIBLE);
            RequestTask requestTask = new RequestTask("/film/" + id, "GET");
            requestTask.setLastTask(() -> {
                Gson Gayson = new Gson();
                String content = requestTask.getResponse().getContent();
                Film film = Gayson.fromJson(content,Film.class);
                binding.setFilm(film);
            });
            requestTask.execute();
            throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setFilms() {
        try {
            binding.layoutFilmInspector.setVisibility(View.VISIBLE);
            binding.layoutFilmInspector.setVisibility(View.GONE);
            RequestTask requestTask = new RequestTask("/film/", "GET");
            requestTask.setLastTask(() -> {
                Gson Gayson = new Gson();
                String content = requestTask.getResponse().getContent();
                Film[] filmek = Gayson.fromJson(content,Film[].class);
                for (Film film: filmek) {
                    binding.layoutFilms.addView(createFilmButton(film));
                }
            });
            requestTask.execute();
            throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFilm(Film film) {
        if (film.getId() != null) {
            sendFilm(film, "PATCH");
            return;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Módosítás");
        alertDialog.setMessage("Elvégzi a módosításokat?");
        alertDialog.setPositiveButton("Igen", (dialogInterface, i) -> {
            Toast.makeText(MainActivity.this, "Film: " + film.toString(), Toast.LENGTH_SHORT).show();
            sendFilm(film, "POST");
        });
        alertDialog.setNegativeButton("Nem", (dialogInterface, i) -> {
            binding.layoutFilmEditor.setVisibility(View.GONE);
        });
        alertDialog.show();
    }

    private void sendFilm(Film film, String method) {
        Log.d("FilmJSON", film.toJson());
        try {
            RequestTask requestTask = new RequestTask("/film" + (film.getId() == null ? "" : "/" + film.getId().toString()), method, film.toJson());
            requestTask.setLastTask(() -> {
                String toastText = "módosítás";
                if (method.equals("POST")) {
                    toastText = "felvétel";
                }
                if (requestTask.getResponse().getCode() < 300) {
                    Toast.makeText(MainActivity.this, "Sikeres " + toastText, Toast.LENGTH_SHORT).show();
                    binding.layoutFilmEditor.setVisibility(View.GONE);
                    return;
                }
                Log.d("Hívás / " + requestTask.getResponse().getCode(), requestTask.getResponse().getContent());
                Toast.makeText(MainActivity.this, "Sikertelen " + toastText, Toast.LENGTH_SHORT).show();
            });
            requestTask.execute();
            setFilms();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFilm(int id) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Törlés");
        alertDialog.setMessage("Biztos törölni szeretné?");
        alertDialog.setPositiveButton("Igen", (dialogInterface, i) -> {
            try {
                RequestTask requestTask = new RequestTask("/film/" + id, "DELETE");
                requestTask.setLastTask(() -> {
                    if (requestTask.getResponse().getCode() < 300) {
                        Toast.makeText(MainActivity.this, "Sikeresen törölve!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("Hívás / " + requestTask.getResponse().getCode(), requestTask.getResponse().getContent());
                    Toast.makeText(MainActivity.this, "Sikertelen törlés!", Toast.LENGTH_SHORT).show();
                });
                requestTask.execute();
                setFilms();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        alertDialog.setNegativeButton("Nem", null);
        alertDialog.show();
    }

    private MaterialButton createFilmButton(Film film) {
        MaterialButton buttonFilm = new MaterialButton(MainActivity.this);
        buttonFilm.setText(film.getCim());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonFilm.setLayoutParams(lp);
        buttonFilm.setOnClickListener(($) -> {
            setFilm(film.getId());
        });
        buttonFilm.setOnLongClickListener(($) -> {
            deleteFilm(film.getId());
            return true;
        });
        return buttonFilm;
    }
}