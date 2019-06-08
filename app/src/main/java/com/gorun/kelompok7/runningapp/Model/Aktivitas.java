package com.gorun.kelompok7.runningapp.Model;

public class Aktivitas {
    public String id, tanggalWaktu, nama;
    public long waktu;
    public double jarak, kecepatan, kaloriTerbakar;

    public Aktivitas(){
    }

    public Aktivitas(String id, String nama, String tanggalWaktu, long waktu, double jarak, double kecepatan, double kaloriTerbakar){
        this.id = id;
        this.nama = nama;
        this.tanggalWaktu = tanggalWaktu;
        this.waktu = waktu;
        this.jarak = jarak;
        this.kecepatan = kecepatan;
        this.kaloriTerbakar = kaloriTerbakar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggalWaktu() {
        return tanggalWaktu;
    }

    public void setTanggalWaktu(String tanggalWaktu) {
        this.tanggalWaktu = tanggalWaktu;
    }

    public long getWaktu() {
        return waktu;
    }

    public void setWaktu(long waktu) {
        this.waktu = waktu;
    }

    public double getJarak() {
        return jarak;
    }

    public void setJarak(double jarak) {
        this.jarak = jarak;
    }

    public double getKecepatan() {
        return kecepatan;
    }

    public void setKecepatan(double kecepatan) {
        this.kecepatan = kecepatan;
    }

    public double getKaloriTerbakar() {
        return kaloriTerbakar;
    }

    public void setKaloriTerbakar(double kaloriTerbakar) {
        this.kaloriTerbakar = kaloriTerbakar;
    }
}
