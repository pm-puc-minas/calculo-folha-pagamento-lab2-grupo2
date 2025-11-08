package com.rh.folhaPagamento.service;

import org.springframework.stereotype.Service;
import java.io.*;

@Service
public class ArquivoService {

    public void serializar(Object obj, String nomeArquivo) {

        try (FileOutputStream fileOut = new FileOutputStream(nomeArquivo);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            out.writeObject(obj);

            System.out.println("Objeto salvo em " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao serializar objeto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Object desserializar(String nomeArquivo) {
        Object obj = null;

        try (FileInputStream fileIn = new FileInputStream(nomeArquivo);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {

            obj = in.readObject();

            System.out.println("Objeto lido de " + nomeArquivo);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao desserializar objeto: " + e.getMessage());
            e.printStackTrace();
        }

        return obj;
    }
}