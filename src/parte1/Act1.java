package parte1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class Act1 {
    
    // Matrices globales para poder usarlas en todo el archivo 
    static int[][] mat1;
    static int[][] mat2;
    static int[][] mat3;
    
    public static void main(String[] args) throws IOException {
        
        // Validar que se reciban los argumentos correctos
        if (args.length < 5) {
            System.out.println("Uso: java parte1.Act1 <nf1> <nc1> <nc2> <tp> <nombreArchivo>");
            System.out.println("Ejemplo: java parte1.Act1 4 6 8 64 referencias/matriz_4x6_8.txt");
            return;
        }
        
        // Para no tener que correr el programa con inputs, se usan los argumentos escritos en consola
        int nf1 = Integer.parseInt(args[0]); // filas matriz 1
        int nc1 = Integer.parseInt(args[1]); // columnas matriz 1 = filas matriz 2
        int nc2 = Integer.parseInt(args[2]); // columnas matriz 2
        int tp = Integer.parseInt(args[3]);  // tamaño de página en bytes
        String nombreArchivo = args[4];
        
        // Crear el directorio referencias si no existe
        File directorio = new File("referencias");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        
        // Establecer el tamaño que van a tener las matrices con las que vamos a trabajar
        mat1 = new int[nf1][nc1];
        mat2 = new int[nc1][nc2];
        mat3 = new int[nf1][nc2];
        
        // Insertar datos de manera aleatoria en ambas matrices
        for (int i = 0; i < nf1; i++)
            for (int j = 0; j < nc1; j++)
                mat1[i][j] = (int)(Math.random() * 10) + 1;

        for (int i = 0; i < nc1; i++)
            for (int j = 0; j < nc2; j++)
                mat2[i][j] = (int)(Math.random() * 10) + 1;
        
        // Multiplicar las matrices
        multiplicar_matrices(nf1, nc1, nc2);
        
        // Mostrar información de tamaños
        System.out.println("-------------------------------------------------------");
        System.out.println("Tamaño en bytes de matriz 1: " + ((nf1 * nc1) * 4));
        System.out.println("Tamaño en bytes de matriz 2: " + ((nc1 * nc2) * 4));
        System.out.println("Tamaño en bytes de matriz 3: " + ((nf1 * nc2) * 4));
        System.out.println("Tamaño de página: " + tp + " bytes");
        System.out.println("Archivo de salida: " + nombreArchivo);
        System.out.println("-------------------------------------------------------");
        
        crearDirecciones(nf1, nc1, nc2, tp, nombreArchivo);
        
        System.out.println("Archivo de referencias generado exitosamente: " + nombreArchivo);
    } 

    public static void mostrarMatrices(int nf1, int nc1, int nc2) {
        System.out.println("=== Matriz 1 (" + nf1 + "x" + nc1 + ") ===");
        for (int i = 0; i < nf1; i++) {
            for (int j = 0; j < nc1; j++) {
                System.out.printf("%4d", mat1[i][j]);
            }
            System.out.println();
        }

        System.out.println("\n=== Matriz 2 (" + nc1 + "x" + nc2 + ") ===");
        for (int i = 0; i < nc1; i++) {
            for (int j = 0; j < nc2; j++) {
                System.out.printf("%4d", mat2[i][j]);
            }
            System.out.println();
        }

        System.out.println("\n=== Matriz 3 - Resultado (" + nf1 + "x" + nc2 + ") ===");
        for (int i = 0; i < nf1; i++) {
            for (int j = 0; j < nc2; j++) {
                System.out.printf("%4d", mat3[i][j]);
            }
            System.out.println();
        }
    }

    public static void multiplicar_matrices(int nf1, int nc1, int nc2) {
        for (int i = 0; i < nf1; i++) {
            for (int j = 0; j < nc2; j++) {
                int acum = 0;
                for (int k = 0; k < nc1; k++) {
                    acum += mat1[i][k] * mat2[k][j];
                }
                mat3[i][j] = acum;
            }
        }
    }
    
    public static void crearDirecciones(int nf1, int nc1, int nc2, int pgSize, String nombreArchivo) throws IOException {
        // Calcular tamaño total de las matrices
        int bytesMat1 = (nf1 * nc1) * 4;
        int bytesMat2 = (nc1 * nc2) * 4;
        int bytesMat3 = (nf1 * nc2) * 4;
        
        // Calcular número de páginas virtuales necesarias
        int paginasTotal = (int) Math.ceil((double) bytesMat1 / pgSize) +
                        (int) Math.ceil((double) bytesMat2 / pgSize) +
                        (int) Math.ceil((double) bytesMat3 / pgSize);
        
        int numReferencias = (nf1 * nc2 * nc1) + (nf1 * nc2 * nc1) + (nf1 * nc2);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            
            // Cabecera
            writer.write("TP=" + pgSize);
            writer.newLine();
            writer.write("NF1=" + nf1);
            writer.newLine();
            writer.write("NC1=" + nc1);
            writer.newLine();
            writer.write("NF2=" + nc1); // NF2 es igual a NC1
            writer.newLine();
            writer.write("NC2=" + nc2);
            writer.newLine();
            writer.write("NR=" + numReferencias);
            writer.newLine();
            writer.write("NP=" + paginasTotal);
            writer.newLine();
            
            // Offsets
            int offsetBaseMat1 = 0;
            int offsetBaseMat2 = bytesMat1;
            int offsetBaseMat3 = bytesMat1 + bytesMat2;
            
            // Generar referencias
            for (int i = 0; i < nf1; i++) {
                for (int j = 0; j < nc2; j++) {
                    for (int k = 0; k < nc1; k++) {
                        // M1[i][k] - Formato: [M1-i-k], pagina, desplazamiento
                        int offsetMat1 = (i * nc1 + k) * 4;
                        int dirVirtualMat1 = offsetBaseMat1 + offsetMat1;
                        int paginaMat1 = dirVirtualMat1 / pgSize;
                        int desplazamientoMat1 = dirVirtualMat1 % pgSize;
                        
                        writer.write(String.format("[M1-%d-%d],%d,%d", i, k, paginaMat1, desplazamientoMat1));
                        writer.newLine();
                        
                        // M2[k][j] - Formato: [M2-k-j], pagina, desplazamiento
                        int offsetMat2 = (k * nc2 + j) * 4;
                        int dirVirtualMat2 = offsetBaseMat2 + offsetMat2;
                        int paginaMat2 = dirVirtualMat2 / pgSize;
                        int desplazamientoMat2 = dirVirtualMat2 % pgSize;
                        
                        writer.write(String.format("[M2-%d-%d],%d,%d", k, j, paginaMat2, desplazamientoMat2));
                        writer.newLine();
                    }
                    // M3[i][j] - Formato: [M3-i-j], pagina, desplazamiento
                    int offsetMat3 = (i * nc2 + j) * 4;
                    int dirVirtualMat3 = offsetBaseMat3 + offsetMat3;
                    int paginaMat3 = dirVirtualMat3 / pgSize;
                    int desplazamientoMat3 = dirVirtualMat3 % pgSize;
                    
                    writer.write(String.format("[M3-%d-%d],%d,%d", i, j, paginaMat3, desplazamientoMat3));
                    writer.newLine();
                }
            }
        }
    }
}