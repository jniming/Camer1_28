package com.example.ipcam.camer.entity;

public class P2pAudio {
    private String file_name;
    private String file_date;
    private int file_count;
    private int file_type;
    private int file_no;
    private int file_size;
    private boolean ischeck;

    public P2pAudio(String file_name, String file_date, int file_count, int file_type, int file_no, int file_size) {
        this.file_name = file_name;
        this.file_date = file_date;
        this.file_count = file_count;
        this.file_type = file_type;
        this.file_no = file_no;
        this.file_size = file_size;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_date() {
        return file_date;
    }

    public void setFile_date(String file_date) {
        this.file_date = file_date;
    }

    public int getFile_count() {
        return file_count;
    }

    public void setFile_count(int file_count) {
        this.file_count = file_count;
    }

    public int getFile_type() {
        return file_type;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public int getFile_no() {
        return file_no;
    }

    public void setFile_no(int file_no) {
        this.file_no = file_no;
    }

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }
}
