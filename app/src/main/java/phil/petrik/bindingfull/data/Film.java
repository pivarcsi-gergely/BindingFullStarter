package phil.petrik.bindingfull.data;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import phil.petrik.bindingfull.BR;

public class Film extends BaseObservable {
    private Integer id;
    private String cim;
    private String kategoria;
    private Integer hossz;
    @SerializedName("ertekeles") //Az api félre van gépelve, szóval...
    private Integer ertekeles;

    public static Film emptyFilm(){
        return new Film(null,null,null,null,null);
    }

    public Film(Integer id, String cim, String kategoria, Integer hossz, Integer ertekels) {
        this.id = id;
        this.cim = cim;
        this.kategoria = kategoria;
        this.hossz = hossz;
        this.ertekeles = ertekels;
    }

    @Bindable
    public Integer getId() {
        return id;
    }

    @Bindable
    public String getCim() {
        return cim;
    }

    @Bindable
    public String getKategoria() {
        return kategoria;
    }

    @Bindable
    public String getHosszString(){
        return hossz==null?"":hossz.toString();
    }

    public Integer getHossz() {
        return hossz;
    }

    @Bindable
    public String getErtekelesString(){
        return ertekeles ==null?"": ertekeles.toString();
    }

    @Bindable
    public Integer getErtekeles() {
        return ertekeles;
    }

    public void setId(Integer id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    public void setCim(String cim) {
        this.cim = cim;
        notifyPropertyChanged(BR.cim);
    }

    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
        notifyPropertyChanged(BR.kategoria);
    }

    public void setHosszString(String hossz) {
        try {
            this.hossz = Integer.parseInt(hossz);
            notifyPropertyChanged(BR.hosszString);
        }
            catch (Exception e){
            this.hossz = 0;
        }
    }

    public void setHossz(int hossz) {
        this.hossz = hossz;
        notifyPropertyChanged(BR.hosszString);
    }

    public void setErtekelesString(String ertekeles) {
        try {
            this.ertekeles = Integer.parseInt(ertekeles);
            notifyPropertyChanged(BR.ertekelesString);
        }
        catch (Exception e){
            this.ertekeles = 0;
        }
    }

    public void setErtekeles(int ertekeles) {
        this.ertekeles = ertekeles;
        notifyPropertyChanged(BR.ertekelesString);
    }

    @NonNull
    @Override
    public String toString() {
        return "id:" + id + ", cim:" + cim + ", kategoria:" + kategoria
                + ", hossz:" + hossz + ", ertekeles:" + ertekeles;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(this);
    }
}
