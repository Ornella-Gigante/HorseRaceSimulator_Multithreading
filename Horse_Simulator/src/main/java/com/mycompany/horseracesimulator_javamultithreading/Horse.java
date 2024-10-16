/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.horseracesimulator_javamultithreading;


/**
 * The Horse class represents a horse in a horse race simulation.
 * Each horse runs in a separate thread, simulating the asynchronous 
 * behavior of multiple horses running at the same time.
 * 
 * The class generates a random race time for each horse, simulates 
 * the running process by pausing the thread for the race duration, 
 * and then prints out the result once the horse finishes the race.
 * 
 * It also provides methods to retrieve the horse's name and the time 
 * it took to complete the race.
 * 
 * @author Ornella Gigante
 */


import java.util.Random;

public class Horse extends Thread {
    private String nombre;
    private int tiempoCarrera;

    public Horse(String nombre) {
        this.nombre = nombre;
    }
    
       /**
     * Method that is executed when the horse's thread starts.
     * It simulates the horse's race by generating a random race time 
     * between 1 and 5 seconds, then pauses the thread for that amount of time.
     * 
     * The race time is simulated with Thread.sleep(), and the horse
     * "runs" for the randomly assigned duration.
     */

    @Override
    public void run() {
        Random rand = new Random();
        tiempoCarrera = rand.nextInt(5) + 1; // Tiempo entre 1 y 5 segundos
        System.out.println("Sale " + nombre);
        try {
            Thread.sleep(tiempoCarrera * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Soy el caballo " + nombre + " he tardado " + tiempoCarrera + " segundos");
    }

    public int getTiempoCarrera() {
        return tiempoCarrera;
    }

    public String getNombre() {
        return nombre;
    }
}