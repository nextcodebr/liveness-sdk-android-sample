package br.com.example.models;

import java.io.Serializable;

public class OCRData implements Serializable {

    private static final long serialVersionUID = 8349471379971135732L;

    private OCRExtraction extraction;

    public OCRExtraction getExtraction() {
        return extraction;
    }

    public void setExtraction(OCRExtraction extraction) {
        this.extraction = extraction;
    }
}
