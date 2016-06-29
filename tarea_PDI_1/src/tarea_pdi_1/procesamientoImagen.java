////////////////////////////////////////////////////////////////
///                                                          ///
///                   José Gregorio Castro                   ///
///                      CI 24.635.907                       ///
///                          Tarea 1                         ///
///                                                          ///
////////////////////////////////////////////////////////////////
package tarea_pdi_1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Twins
 */
public class procesamientoImagen {
    
    private BufferedImage imageActual;
    public int alto, ancho, relleno, tam;
    public byte[] bytes;
    public String dirName="C:\\Users\\Twins\\Documents";
    
    
    public BufferedImage abrir() throws FileNotFoundException, IOException
    {
//        BufferedImage imageActual;
        //Creamos un nuevo cuadro de diálogo para seleccionar imagen
        JFileChooser selector=new JFileChooser();
        //Le damos un título
        selector.setDialogTitle("Seleccione una imagen");
        //Filtramos los tipos de archivos
        FileNameExtensionFilter filtroImagen = new FileNameExtensionFilter("BMP","bmp");
        selector.setFileFilter(filtroImagen);
        //Abrimos el cuadro de diálog
        int flag=selector.showOpenDialog(null);
        //Comprobamos que pulse en aceptar
        if(flag==JFileChooser.APPROVE_OPTION)
        {
            InputStream ios = null;
            try 
            {
                File imagenSeleccionada=selector.getSelectedFile();
                bytes = new byte[(int) imagenSeleccionada.length()];
                ios = new FileInputStream(imagenSeleccionada);
                if (ios.read(bytes) == -1) {
                    throw new IOException("EOF reached while trying to read the whole file");
                }
            } finally {
                try {
                    
                    if (ios != null)
                        ios.close();
                } catch (IOException e) {
                }
            }
            
            InputStream in = new ByteArrayInputStream(bytes);
            imageActual = ImageIO.read(in);
            
            System.out.println(bytes[28] + " bits");
            
            //Obtengo alto y ancho
            ancho = (((int)bytes[21]&0xff) << 24) | (((int)bytes[20]&0xff) << 16) | (((int)bytes[19]&0xff) << 8) | (int)bytes[18]&0xff;
            alto = (((int)bytes[25]&0xff) << 24) | (((int)bytes[24]&0xff) << 16) | (((int)bytes[23]&0xff) << 8) | (int)bytes[22]&0xff;
//            tam = bytes.length - 54;

            System.out.println("Alto = "+ alto + " Ancho = " + ancho);
            

        }
        
        
        
        
        return imageActual;
    }
    
    
    public void aplicarNegativo(int x, int N) {
        for(int i=x;i<N;i++)
            bytes[i] = (byte) (255 - bytes[i]);
    }
    
    public BufferedImage Negativo() throws IOException 
    {
        //bytes[28] me dice cuantos bits son (2,4,8,24)
        if (bytes[28] == 24) {
            tam = bytes.length;
            aplicarNegativo(54,tam);
        } else if (bytes[28] == 8) {
            aplicarNegativo(54,1078);
        } else if (bytes[28] == 4) {
            aplicarNegativo(54,118);
        } else if (bytes[28] == 1) {
            aplicarNegativo(54,64);
        }
        
        //Paso la imagen en bytes a BufferedImage
        InputStream in = new ByteArrayInputStream(bytes);
        imageActual = ImageIO.read(in);
        
        BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytes));
        ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
        
        
        return imageActual;
    }
    
    public BufferedImage Invertir() throws IOException 
    {
        int limite, pos, aux, pos_arriba, fin, N, aux2, ancho2;
        if (bytes[28] == 24)
        {
            relleno = relleno24bits();
            byte R, G, B;
            if (alto%2 == 0)
                limite = alto/2;
            else
                limite = (alto/2)+1;
            pos = 54;
            aux = 54;
            pos_arriba = bytes.length-relleno-1;

            if (alto%2 == 0)
                fin = limite*ancho;
            else
                fin = limite*ancho-(ancho/2);

            for (int i=0;i<fin;i++)
            {
                if (relleno!=0 && pos==aux+(ancho*3)){
                    pos += relleno;
                    pos_arriba -= relleno;
                    aux = pos;
                }
                R = bytes[pos_arriba];
                G = bytes[pos_arriba-1];
                B = bytes[pos_arriba-2];
                bytes[pos_arriba] = bytes[pos+2];
                bytes[pos_arriba-1] = bytes[pos+1];
                bytes[pos_arriba-2] = bytes[pos];

                bytes[pos] = B;
                bytes[pos+1] = G;
                bytes[pos+2] = R;

                pos += 3;
                pos_arriba -= 3;
            }

        } else if (bytes[28] == 8)
        {
            relleno = relleno8bits();

            if (alto%2 == 0){
                limite = (int)alto/2;
                N = limite*(ancho);
            } else {
                limite = (int)(alto/2)+1;
                N = limite*ancho-(ancho/2);
            }
            pos = 1078;
            aux = pos;
            pos_arriba = bytes.length - relleno - 1;
            
            for (int i=0;i<N;i++){
                if (pos == aux+ancho && relleno != 0){
                    pos += relleno;
                    pos_arriba = pos_arriba - relleno;
                    aux = pos;
                }
            aux2 = bytes[pos_arriba];
            bytes[pos_arriba] = bytes[pos];
            bytes[pos] = (byte) aux2;
            pos++;
            pos_arriba--;

            }


        } else if (bytes[28] == 4) 
        {
            String aux_bits = "";
            relleno = relleno4bits();
            boolean impar;
            
                
            if (ancho%2 ==  0) {
                ancho2 = ancho/2;
                impar = false;
            } else {
                ancho2 = (ancho/2)+1;
                impar = true;
            }
            int n;
            if (alto%2 == 0){
                limite = alto/2;
                n = limite*ancho2;
            } else {
                limite = (alto/2)+1;
                n = (limite * ancho2) - (ancho2/2);
            }
                
                
            
            pos = 118;
            aux = 118;
            pos_arriba = bytes.length-1-relleno;
            String by, by_final,by_anterior, pri_cuatro, seg_cuatro, bits_aux="";
//            int n = limite*ancho2;
            if (impar)
            {
                for (int i=0;i<n;i++)
                {
                    if (pos == aux)
                    {
                        //Aqui estoy en la primera posicion:
                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_arriba]&0xff))).replace(' ', '0');
                        
                        pri_cuatro = by.substring(0, 4);
                        seg_cuatro = by_final.substring(0,4);
                        bits_aux = by.substring(4,8);
                        
                        bytes[pos] =  (byte)Integer.parseInt((seg_cuatro + bits_aux),2);
                        bytes[pos_arriba] = (byte)Integer.parseInt((pri_cuatro + by_final.substring(4,8)),2);
                        
                        pos++;
                        pos_arriba--;

                    }
                    else if (pos == aux+ancho2 && relleno!=0)
                    {
                        pos+=relleno;
                        pos_arriba-=relleno;
                        aux = pos;
                    }
                    else
                    {
                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_arriba]&0xff))).replace(' ', '0');
                        by_anterior = String.format("%8s", Integer.toBinaryString(((int)bytes[pos-1]&0xff))).replace(' ', '0');
                        
                        bytes[pos-1] = (byte)Integer.parseInt(by_anterior.substring(0,4)+by_final.substring(4,8),2);
                        bytes[pos_arriba] = (byte)Integer.parseInt(by.substring(0,4)+bits_aux,2);
                        bits_aux = by.substring(4,8);
                        bytes[pos] = (byte)Integer.parseInt(by_final.substring(0,4)+bits_aux,2);
                        pos++;
                        pos_arriba--;
                    }
                }
            } else
            {
                //caso par
                for (int i=0;i<n;i++)
                {
                    if(pos == aux+ancho2 && relleno!=0)
                    {
                        pos+=relleno;
                        pos_arriba-=relleno;
                        aux = pos;
                    }
                    else
                    {
                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_arriba]&0xff))).replace(' ', '0');
                        
                        bytes[pos_arriba] = (byte)Integer.parseInt(by.substring(4,8)+by.substring(0,4),2);
                        bytes[pos] = (byte)Integer.parseInt(by_final.substring(4,8)+by_final.substring(0,4),2);
                        pos++;
                        pos_arriba--;
                    }
                        
                }
            }


        } else if (bytes[28] == 1)
        {
            relleno = relleno1bits();
            String aux_bits = "";
            boolean impar;
            if (alto%2 == 0)
                limite = alto/2;
            else
                limite = (alto/2)+1;
            if (ancho%8 == 0)
            {
                ancho2 = ancho/8;
                impar = false;
            } else
            {
                ancho2 = (ancho/8)+1;
                impar = true;
            } 
            pos = 62;
            aux = 62;
            pos_arriba = bytes.length-1-relleno;
            int ancho_aux = ancho;
            String by, by_final,by_anterior, pri_cuatro, seg_cuatro, bits_aux="", cambio,cambio_final;
            int n = (limite*ancho2) - (ancho2/2);
            while (ancho_aux > 8)
                ancho_aux -= 8;
            
          
            if (impar)
            {
                for (int i=0;i<n;i++)
                {
                    if (pos == aux)
                    {
                        //Aqui estoy en la primera posicion:
                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_arriba]&0xff))).replace(' ', '0');
                        
                        cambio = by.substring(0, ancho_aux);
                        cambio_final = by_final.substring(0,ancho_aux);
                        
                        StringBuilder builder=new StringBuilder(cambio);
                        cambio = builder.reverse().toString();
                        builder=new StringBuilder(cambio_final);
                        cambio_final = builder.reverse().toString();
                        
                        
                   
                        
                        bytes[pos] =  (byte)Integer.parseInt((cambio_final + by.substring(ancho_aux,8)),2);
                        bytes[pos_arriba] = (byte)Integer.parseInt((cambio + by_final.substring(ancho_aux,8)),2);
                        bits_aux = by.substring(ancho_aux,8);
                        
                        pos++;
                        pos_arriba--;

                    }
                    else if (pos == aux+ancho2 && relleno!=0)
                    {
                        pos+=relleno;
                        pos_arriba-=relleno;
                        aux = pos;
                    }
                    else
                    {

                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_arriba]&0xff))).replace(' ', '0');
                        by_anterior = String.format("%8s", Integer.toBinaryString(((int)bytes[pos-1]&0xff))).replace(' ', '0');
                        
                        cambio = by.substring(0, ancho_aux);
                        cambio_final = by_final.substring(ancho_aux,8);
                        
                        StringBuilder builder=new StringBuilder(cambio);
                        cambio = builder.reverse().toString();
                        builder=new StringBuilder(cambio_final);
                        cambio_final = builder.reverse().toString();
                        builder=new StringBuilder(bits_aux);
                        bits_aux = builder.reverse().toString();
                        
                        bytes[pos-1] = (byte)Integer.parseInt(by_anterior.substring(0,ancho_aux)+cambio_final,2);
                        bytes[pos_arriba] = (byte)Integer.parseInt(cambio+bits_aux,2);
                        bits_aux = by.substring(ancho_aux,8);
                        String x = by_final.substring(0,ancho_aux);
                        builder=new StringBuilder(x);
                        x = builder.reverse().toString();
                        bytes[pos] = (byte)Integer.parseInt(x+bits_aux,2);
                        pos++;
                        pos_arriba--;
                    }
                }
            } else
            {
                //caso par
                for (int i=0;i<n;i++)
                {
                    if(pos == aux+ancho2 && relleno!=0)
                    {
                        pos+=relleno;
                        pos_arriba-=relleno;
                        aux = pos;
                    }
                    else
                    {
                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_arriba]&0xff))).replace(' ', '0');
                        
                        String x = by.substring(0,ancho_aux);
                        StringBuilder builder = new StringBuilder(x);
                        x = builder.reverse().toString();
                        
                        
                        
                        bytes[pos_arriba] = (byte)Integer.parseInt(x,2);
                        x = by_final.substring(0,ancho_aux);
                        builder = new StringBuilder(x);
                        x = builder.reverse().toString();
                        bytes[pos] = (byte)Integer.parseInt(x,2);
                        pos++;
                        pos_arriba--;
                    }
                        
                }
            }  
        }
        
        //Paso la imagen en bytes a BufferedImage
        InputStream in = new ByteArrayInputStream(bytes);
        imageActual = ImageIO.read(in);
        
        
        BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytes));
        ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
        
        return imageActual;
    }
    
    public BufferedImage voltear90() throws IOException, InterruptedException
    {
        
        
        if (bytes[28] == 24)
        {

            int i = alto, l = 1, j = ancho * 3, k = 0, auxiliar, temp, relleno_nuevo;
            relleno = relleno24bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            relleno_nuevo = relleno24bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            tam = bytes.length - 54;
            byte[] bytes_aux = new byte[tam];
            int ind = 54;
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);
           
            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }
            byte[] bytes2 = new byte[n];
            tam = n;
            
            while (k < tam)
            {
                if (i == 0) 
                {
                    i = alto;
                    l = 1;
                    j-=3;
                    for (int w = k; w < k + relleno_nuevo; w++)
                            bytes2[k] = 0x00;
                    k += relleno_nuevo;
                } else
                {
                temp = ((ancho * 3) * i) - j + (relleno * (alto - l));
                bytes2[k] = bytes_aux[temp];
                bytes2[k + 1] = bytes_aux[temp + 1];
                bytes2[k + 2] = bytes_aux[temp + 2];
                k+=3;
                i--;
                l++;
                }
            }
            
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            byte res[] = new byte[n+54];
            
            System.arraycopy(bytes, 0, res, 0, 54);
            
            int pos = 54;
            for (int p=0;p<n;p++){
                res[pos] = bytes2[p];
                pos++;
            }
            
            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;
            
            bytes = new byte[n+54];
            System.arraycopy(res, 0, bytes, 0, n+54);

            try 
            {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
                
                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
                
   
            }
            catch (EOFException ex1) {
//                break; //EOF reached.
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }
            
            
        } else if (bytes[28] == 8)
        {
            int i = alto, l = 1, j = ancho, k = 0, auxiliar, relleno_nuevo;
            
            relleno = 4 - (ancho % 4);
            relleno_nuevo = 4 - (alto % 4);
         
            if (relleno_nuevo == 4)
                relleno_nuevo = 0;
            if (relleno == 4)
                relleno = 0;
            
            tam = bytes.length - 1078;
            byte[] bytes_aux = new byte[tam];
            int ind = 1078;
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);
           
        
            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }
            byte[] bytes2 = new byte[n];
            tam = n;
            
            
            while (k < tam)
            {
                if (i == 0) {
                    i = alto;
                    l = 1;
                    j--;
                    for (int w = k; w < relleno_nuevo+k; w++)
                        bytes2[k] = 0;
                    k += relleno_nuevo;
                } else
                {
                    bytes2[k] = bytes_aux[(ancho * i) - j + (relleno * (alto - l) )];
                    k++;
                    l++;
                    i--;
                }
            }
            
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            byte res[] = new byte[n+1078];
            
            System.arraycopy(bytes, 0, res, 0, 1078);
            
            int pos = 1078;
            for (int p=0;p<n;p++){
                res[pos] = bytes2[p];
                pos++;
            }
            
            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;
            
            bytes = new byte[n+1078];
            System.arraycopy(res, 0, bytes, 0, n+1078);

            try 
            {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
                
                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
                
   
            }
            catch (EOFException ex1) {
//                break; //EOF reached.
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }
            
            
            
            
        } else if (bytes[28] == 4)
        {
            int i = 1, l = 0, j = 1, k = 0, auxiliar, relleno_nuevo;
            
            relleno = relleno4bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            relleno_nuevo = relleno4bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;

            tam = bytes.length - 118;
            byte[] bytes_aux = new byte[tam];
            int ind = 118;
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);
            
            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }
            
            tam = n;
            float aux_ancho = ancho;
            
            int pos = (int) (Math.ceil(aux_ancho/2) -1);
            int aux_up = pos;
            boolean primero = true;
            int guardado = 0;
            int limite = (int) (bytes_aux.length - Math.ceil(aux_ancho/2) + relleno);
            int impar;
            String bits = "";
            String bits_auxiliar;
            int indice = 0;
            
            if (ancho%2 != 0)
            {
                for (i=0;i<alto;i++)
                {
                    if (pos>limite)
                    {
                        if (guardado==0)
                        {
                            guardado = 0;
                            aux_up--;
                            pos = aux_up;
                            indice++;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');

                            aux_up++;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        }
                        for (j=0;j<relleno_nuevo;j++)
                            indice++;
                        //Aqui pone un break;
                    } else
                    {
                        if (guardado==0)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bits = bits_auxiliar.substring(0,4);
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 1;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 0;
                            indice++;
                        }
                    }
                }
                impar = alto;
            } else
                impar = 0;
            
            int N = (alto*ancho)-impar;
            for (i=0;i<N;i++)
            {
                if (pos >= limite)
                {
                    if (guardado == 0)
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            indice++;
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                        } else
                        {
                            indice++;
                            primero = true;
                            aux_up--;
                            guardado = 0;
                            pos = aux_up;
                        }
                    } else
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            indice++;
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            indice++;
                            primero = true;
                            aux_up--;
                            guardado = 0;
                            pos = aux_up;
                        }
                    }
                    for (j=0;j<relleno_nuevo;j++)
                    {
                        indice++;
                    }
                } else
                {
                    if (guardado == 0)
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bits = bits_auxiliar.substring(4,8);
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 1;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bits = bits_auxiliar.substring(0,4);
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 1;
                        }
                    } else
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            indice++;
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 0;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            indice++;
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 0;
                        }
                        
                    }
                }
            }
            
            byte[] bytes2 = new byte[indice];
            indice = 0;
            pos = (int) (Math.ceil(aux_ancho/2) -1);
            aux_up = pos;
            primero = true;
            guardado = 0;
            bits = "";
            
            if (ancho%2 != 0)
            {
                for (i=0;i<alto;i++)
                {
                    if (pos>limite)
                    {
                        if (guardado==0)
                        {
                            bytes2[indice] =  (byte) (bytes_aux[pos]&240);
                            guardado = 0;
                            aux_up--;
                            pos = aux_up;
                            indice++;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits+bits_auxiliar.substring(0,4)),2);
                            aux_up++;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        }
                        for (j=0;j<relleno_nuevo;j++)
                        {
                            bytes2[indice] = 0;
                            indice++;
                        }
                            
                        //Aqui pone un break;
                    } else
                    {
                        if (guardado==0)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bits = bits_auxiliar.substring(0,4);
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 1;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits+bits_auxiliar.substring(0,4)),2);
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 0;
                            indice++;
                        }
                    }
                }
                impar = alto;
            } else
                impar = 0;
            
            N = (alto*ancho)-impar;
            for (i=0;i<N;i++)
            {
                if (pos >= limite)
                {
                    if (guardado == 0)
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits_auxiliar.substring(4,8)+"0000"),2);
                            indice++;
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                        } else
                        {
                            bytes2[indice] =  (byte) (bytes_aux[pos]&240);
                            indice++;
                            primero = true;
                            aux_up--;
                            guardado = 0;
                            pos = aux_up;
                        }
                    } else
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits+bits_auxiliar.substring(4,8)),2);
                            indice++;
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits+bits_auxiliar.substring(0,4)),2);
                            indice++;
                            primero = true;
                            aux_up--;
                            guardado = 0;
                            pos = aux_up;
                        }
                    }
                    for (j=0;j<relleno_nuevo;j++)
                    {
                        indice++;
                    }
                } else
                {
                    if (guardado == 0)
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bits = bits_auxiliar.substring(4,8);
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 1;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bits = bits_auxiliar.substring(0,4);
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 1;
                        }
                    } else
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits+bits_auxiliar.substring(4,8)),2);
                            indice++;
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 0;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits+bits_auxiliar.substring(0,4)),2);
                            indice++;
                            pos = (int) (pos + relleno + Math.ceil(aux_ancho/2));
                            guardado = 0;
                        }
                        
                    }
                }
            }
            
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            byte res[] = new byte[indice+118];
            
            System.arraycopy(bytes, 0, res, 0, 118);
            
            pos = 118;
            for (int p=0;p<indice;p++){
                res[pos] = bytes2[p];
                pos++;
            }
            
            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;
            
            bytes = new byte[indice+118];
            System.arraycopy(res, 0, bytes, 0, indice+118);
            
            
            try {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
                
                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
            }
            catch (EOFException ex1) {
//                break; //EOF reached.
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }

            
            
        } else if (bytes[28] == 1)
        {
            int auxiliar, relleno_nuevo;
            relleno = relleno1bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            relleno_nuevo = relleno1bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;

            tam = bytes.length - 62;
            byte[] bytes_aux = new byte[tam];
            int ind = 62;
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);
            
            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }
            
            tam = n;
            float aux_ancho = ancho;
            
            int pos = (int) (bytes_aux.length - relleno - Math.ceil(aux_ancho/8));
            int aux_up = pos;
            int guardado = 0;
            int limite = (int) Math.ceil(aux_ancho/8);
            int primero = 0;
            String bits = "";
            String bits_auxiliar, relleno_aux="";
            int N = alto*ancho;
            int indice = 0;
            
            for (int i=0;i<N;i++)
            {
                if (pos < limite)
                {
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        relleno_aux = "";
                        for (int j = guardado+1 ; j<8 ; j++)
                            relleno_aux += '0';
                        
                        indice++;
                    } else
                    {
                        indice++;
                    }
                    guardado = 0;
                    bits = "";
                    primero += 1;
                    if (primero == 8) {
                        aux_up++;
                        primero = 0;
                    }
                    pos = aux_up;
                    for (int j = 0 ; j<relleno_nuevo ; j++) {
                        indice++;
                    }
                        
                } else
                {
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        guardado += 1;
                    } else
                    {
                        indice++;
                        bits = "";
                        guardado = 0;
                    }
                    pos = (int) (pos - relleno - Math.ceil(aux_ancho/8));
                }
            }
            
            
            byte[] bytes2 = new byte[indice];
            indice = 0;
            pos = (int) (bytes_aux.length - relleno - Math.ceil(aux_ancho/8));
            aux_up = pos;
            guardado = 0;
            primero = 0;
            bits = "";
            relleno_aux="";
            
            for (int i=0;i<N;i++)
            {
                if (pos < limite)
                {
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        relleno_aux = "";
                        for (int j = guardado+1 ; j<8 ; j++)
                            relleno_aux += '0';
                        
                        bytes2[indice] =  (byte)Integer.parseInt((bits + relleno_aux ),2);
                        indice++;
                    } else
                    {
                        bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
                        indice++;
                    }
                    guardado = 0;
                    bits = "";
                    primero += 1;
                    if (primero == 8) {
                        aux_up++;
                        primero = 0;
                    }
                    pos = aux_up;
                    for (int j = 0 ; j<relleno_nuevo ; j++) {
                        bytes2[indice] = 0;
                        indice++;
                    }
                        
                } else
                {
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        guardado += 1;
                    } else
                    {
                        
                        bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
                        indice++;
                        bits = "";
                        guardado = 0;
                    }
                    pos = (int) (pos - relleno - Math.ceil(aux_ancho/8));
                }
            }
            
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            byte res[] = new byte[indice+62];
            
            System.arraycopy(bytes, 0, res, 0, 62);
            
            pos = 62;
            for (int p=0;p<indice;p++){
                res[pos] = bytes2[p];
                pos++;
            }
            
            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;
            
            bytes = new byte[indice+62];
            System.arraycopy(res, 0, bytes, 0, indice+62);
            
            
            try {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
                
                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
            }
            catch (EOFException ex1) {
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }
            
            
        }
            
        
        
        
        
        
        
        
        return imageActual;
    }
    
    public BufferedImage voltear270() throws IOException, InterruptedException
    {
        int i = 1, l = 0, j = 1, k = 0, auxiliar, relleno_nuevo;
        
        if (bytes[28] == 24)
        {
            j = 3;
            relleno = relleno24bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            relleno_nuevo = relleno24bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            tam = bytes.length - 54;

            
            byte[] bytes_aux = new byte[tam];
            int ind = 54;
            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);
            byte[] bytes2 = new byte[n];
            tam = n;
            
            while (k < tam)
            {
                if (i == alto + 1) {   
                    i = 1;
                    l = 0;
                    j+=3;
                    for (int w = k; w < relleno_nuevo+k; w++)
                        bytes2[k] = 0;
                        
                    k += relleno_nuevo;
                } else
                {
                    
                
                auxiliar = ((ancho * 3) * i) - j + (relleno * l) ;
                bytes2[k] = bytes_aux[auxiliar];
                bytes2[k + 1] = bytes_aux[auxiliar + 1];
                bytes2[k + 2] = bytes_aux[auxiliar + 2];

                
                k += 3;
                l++;
                i++;
                }
            }
            
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
                        
            byte res[] = new byte[n+54];
            
            System.arraycopy(bytes, 0, res, 0, 54);
            
            int pos = 54;
            for (int p=0;p<n;p++){
                res[pos] = bytes2[p];
                pos++;
            }
            
            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;
            
            bytes = new byte[n+54];
            System.arraycopy(res, 0, bytes, 0, n+54);
            
            try 
            {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
                
                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
                
   
            }
            catch (EOFException ex1) {
//                break; //EOF reached.
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }
            
        } else if (bytes[28] == 8)
        {
            
            relleno = 4 - (ancho % 4);
            relleno_nuevo = 4 - (alto % 4);
         
            if (relleno_nuevo == 4)
                relleno_nuevo = 0;
            if (relleno == 4)
                relleno = 0;
            
            tam = bytes.length - 1078;
            byte[] bytes_aux = new byte[tam];
            int ind = 1078;
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);
            
            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }
            byte[] bytes2 = new byte[n];
            tam = n;
            
            while (k < tam)
            {
                
                if (i == alto + 1) {
                    i = 1;
                    l = 0;
                    j++;
                    for (int w = k; w < relleno_nuevo+k; w++)
                        bytes2[k] = 0;
                    k += relleno_nuevo;
                } else
                {
                    bytes2[k] = bytes_aux[(ancho * i) - j + (relleno * l)];
                    k++;
                    l++;
                    i++;
                }
            }
            
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            byte res[] = new byte[n+1078];
            
            System.arraycopy(bytes, 0, res, 0, 1078);
            
            int pos = 1078;
            for (int p=0;p<n;p++){
                res[pos] = bytes2[p];
                pos++;
            }
            
            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;
            
            bytes = new byte[n+1078];
            System.arraycopy(res, 0, bytes, 0, n+1078);
            
            try {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
                
                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
            }
            catch (EOFException ex1) {
//                break; //EOF reached.
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }
        } else if (bytes[28] == 4)
        {
            relleno = relleno4bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            relleno_nuevo = relleno4bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;

            tam = bytes.length - 118;
            byte[] bytes_aux = new byte[tam];
            int ind = 118;
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);
            
            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }
            
            tam = n;
            float aux_ancho = ancho;
            
            int pos = (int) (bytes_aux.length - relleno - Math.ceil(aux_ancho/2));
            int aux_up = pos;
            boolean primero = true;
            int guardado = 0;
            String bits = "";
            String bits_auxiliar;
            int limite = (int) Math.ceil(aux_ancho/2);
            
            int N = alto*ancho;
            int indice = 0;
            
            
            for (int in=0;in<N;in++)
            {
                if (pos<limite)
                {
                    if (guardado==0)
                    {
                        if (primero)
                        {
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            primero = true;
                            aux_up++;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        }
                    } else
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            primero = true;
                            aux_up++;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        }
                    }
                    
                    for (j = 0; j<relleno_nuevo; j++)
                    {
                        indice++;
                    }
                } else
                {
                    if (guardado == 0)
                    {
                        bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (primero)
                            bits = bits_auxiliar.substring(0,4);
                        else
                            bits = bits_auxiliar.substring(4,8);
                        
                        pos = (int) (pos - relleno - Math.ceil(aux_ancho/2));
                        guardado = 1;
                    }
                    else
                    {
                        bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (primero)
                        {
                            indice++;
                        } else
                        {
                            indice++;
                        }
                        pos = (int) (pos - relleno - Math.ceil(aux_ancho/2));
                        guardado = 0;
                    }
                }
            }
            byte[] bytes2 = new byte[indice];
            
            indice = 0;
            
            pos = (int) (bytes_aux.length - relleno - Math.ceil(aux_ancho/2));
            aux_up = pos;
            primero = true;
            guardado = 0;
            bits = "";
            for (int in=0;in<N;in++)
            {
                if (pos<limite)
                {
                    if (guardado==0)
                    {
                        if (primero)
                        {
                            bytes2[indice] = (byte) (bytes_aux[pos]&240);
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits_auxiliar.substring(4,8)+"0000"),2);
                            primero = true;
                            aux_up++;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        }
                    } else
                    {
                        if (primero)
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.substring(0,4)),2);
                            primero = false;
                            guardado = 0;
                            pos = aux_up;
                            indice++;
                        } else
                        {
                            bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                            bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.substring(4,8)),2);
                            primero = true;
                            guardado = 0;
                            aux_up++;
                            pos = aux_up;
                            indice++;
                        }
                    }
                    
                    for (j = 0; j<relleno_nuevo; j++)
                    {
                        bytes2[indice] = 0;
                        indice++;
                    }
                } else
                {
                    if (guardado == 0)
                    {
                        bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (primero)
                            bits = bits_auxiliar.substring(0,4);
                        else
                            bits = bits_auxiliar.substring(4,8);
                        
                        pos = (int) (pos - relleno - Math.ceil(aux_ancho/2));
                        guardado = 1;
                    }
                    else
                    {
                        bits_auxiliar = String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (primero)
                        {
                            bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.substring(0,4)),2);
                            indice++;
                        } else
                        {
                            bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.substring(4,8)),2);
                            indice++;
                        }
                        pos = (int) (pos - relleno - Math.ceil(aux_ancho/2));
                        guardado = 0;
                    }
                }
            }
            
                        
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            byte res[] = new byte[indice+118];
                        
            System.arraycopy(bytes, 0, res, 0, 118);
            
            pos = 118;
            for (int p=0;p<indice;p++){
                res[pos] = bytes2[p];
                pos++;
            }
            
            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;
            
            bytes = new byte[indice+118];
            System.arraycopy(res, 0, bytes, 0, indice+118);
            
            
            try {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
                
                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
            }
            catch (EOFException ex1) {
//                break; //EOF reached.
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }
            
        } else if (bytes[28] == 1)
        {
            relleno = relleno1bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            relleno_nuevo = relleno1bits();
            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;
            
            
            tam = bytes.length - 62;
            byte[] bytes_aux = new byte[tam];
            int ind = 62;
            int n = tam + (ancho * relleno_nuevo) - (alto * relleno);

            for (int p=0;p<tam;p++){
                bytes_aux[p] = bytes[ind];
                ind++;
            }

            tam = n;
            float aux_ancho = ancho;
            int pos = (int) (Math.ceil(aux_ancho/8) - 1);
            int aux_up = pos;
            int guardado = 0;
            int limite =bytes_aux.length - ( (int)Math.ceil(aux_ancho/8) + relleno ) ;
            int primero;
            String bits = "";
            String bits_auxiliar, relleno_aux="";
            int N;
            int indice = 0;
            int ancho_aux = ancho;
            while (ancho_aux>8)
                ancho_aux -= 8;
            
            
            int impar;
            primero = ancho_aux - 1;

            
            if (ancho%8 != 0)
            {
                N = alto*ancho_aux;
                for (i=0;i<N;i++)
                {
                    if (pos >= limite)
                    {
                        bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (guardado <= 6)
                        {
                            bits = bits + bits_auxiliar.charAt(primero);
                            relleno_aux = "";
                            for (j = guardado+1 ; j<8 ; j++)
                                relleno_aux += '0';
                            indice++;
                        } else
                        {
                            indice++;
                        }
                        guardado = 0;
                        bits = "";
                        primero -= 1;

                        pos = aux_up;
                        for (j = 0 ; j<relleno_nuevo ; j++) {
                            indice++;
                        }
                    } else
                    {
                        bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (guardado <= 6)
                        {
                            bits = bits + bits_auxiliar.charAt(primero);
                            guardado += 1;
                        } else
                        {
                            indice++;
                            bits = "";
                            guardado = 0;
                        }
                        pos = (int) (pos + relleno + Math.ceil(aux_ancho/8));
                    }
                }
                
                impar = alto*ancho_aux;
                aux_up--;
                pos = aux_up;
                
            } else{
                impar = 0;
            }
            N = alto*ancho-impar;
            primero = 7;
            for (i=0;i<N;i++)
            {
                if (pos >= limite)
                {   
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        relleno_aux = "";
                        for (j = guardado+1 ; j<8 ; j++)
                            relleno_aux += '0';
                        indice++;
                    } else
                    {
                        indice++;
                    }
                    guardado = 0;
                    bits = "";
                    primero -= 1;
                    if (primero == -1) {
                        aux_up--;
                        primero = 7;
                    }
                    pos = aux_up;
                    for (j = 0 ; j<relleno_nuevo ; j++) {
                        indice++;
                    }
                } else
                {
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        guardado += 1;
                    } else
                    {
                        indice++;
                        bits = "";
                        guardado = 0;
                    }
                    pos = (int) (pos + relleno + Math.ceil(aux_ancho/8));
                    
                }
            }
            
            byte[] bytes2 = new byte[indice];
            indice = 0;
            pos = (int) (Math.ceil(aux_ancho/8) - 1);
            aux_up = pos;
            guardado = 0;
            primero = 7;
            bits = "";
            relleno_aux="";
            primero = ancho_aux - 1;
            
            
            if (ancho%8 != 0)
            {
                
                N = alto*ancho_aux;
                for (i=0;i<N;i++)
                {
                    if (pos >= limite)
                    {
                        bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (guardado <= 6)
                        {
                            bits = bits + bits_auxiliar.charAt(primero);
                            relleno_aux = "";
                            for (j = guardado+1 ; j<8 ; j++)
                                relleno_aux += '0';
                            bytes2[indice] =  (byte)Integer.parseInt((bits + relleno_aux ),2);
                            indice++;
                        } else
                        {
                            bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
                            indice++;
                        }
                        guardado = 0;
                        bits = "";
                        primero -= 1;

                        pos = aux_up;
                        for (j = 0 ; j<relleno_nuevo ; j++) {
                            bytes2[indice] =  0;
                            indice++;
                        }
                    } else
                    {
                        bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                        if (guardado <= 6)
                        {
                            bits = bits + bits_auxiliar.charAt(primero);
                            guardado += 1;
                        } else
                        {
                            bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
                            indice++;
                            bits = "";
                            guardado = 0;
                        }
                        pos = (int) (pos + relleno + Math.ceil(aux_ancho/8));
                    }
                }
                
                impar = alto*ancho_aux;
                aux_up--;
                pos = aux_up;
                
            } else
                impar = 0;
            
            N = alto*ancho-impar;
            primero = 7;
            for (i=0;i<N;i++)
            {
                if (pos >= limite)
                {
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        relleno_aux = "";
                        for (j = guardado+1 ; j<8 ; j++)
                            relleno_aux += '0';
                        bytes2[indice] =  (byte)Integer.parseInt((bits + relleno_aux ),2);
                        indice++;
                    } else
                    {
                        bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
                        indice++;
                    }
                    guardado = 0;
                    bits = "";
                    primero -= 1;
                    if (primero == -1) {
                        aux_up--;
                        primero = 7;
                    }
                    pos = aux_up;
                    for (j = 0 ; j<relleno_nuevo ; j++) {
                        bytes2[indice] =  0;
                        indice++;
                    }
                } else
                {
                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
                    if (guardado <= 6)
                    {
                        bits = bits + bits_auxiliar.charAt(primero);
                        guardado += 1;
                    } else
                    {
                        bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
                        indice++;
                        bits = "";
                        guardado = 0;
                    }
                    pos = (int) (pos + relleno + Math.ceil(aux_ancho/8));
//                    if (pos >= 256000)
//                        pos = 255999;
                }
            }
            
            
//            for (i=0;i<N;i++)
//            {
//                if (pos >= limite)
//                {
//                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
//                    if (guardado <= 6)
//                    {
//                        bits = bits + bits_auxiliar.charAt(primero);
//                        relleno_aux = "";
//                        for (j = guardado+1 ; j<8 ; j++)
//                            relleno_aux += '0';
//                        bytes2[indice] =  (byte)Integer.parseInt((bits + relleno_aux ),2);
//                        indice++;
//                    } else
//                    {
//                        bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
//                        indice++;
//                    }
//                    guardado = 0;
//                    bits = "";
//                    primero -= 1;
//                    if (primero == -1) {
//                        aux_up--;
//                        primero = 7;
//                    }
//                    pos = aux_up;
//                    for (j = 0 ; j<relleno_nuevo ; j++) {
//                        bytes2[indice] =  0;
//                        indice++;
//                    }
//                } else
//                {
//                    bits_auxiliar =  String.format("%8s", Integer.toBinaryString(((int)bytes_aux[pos]&0xff))).replace(' ', '0');
//                    if (guardado <= 6)
//                    {
//                        bits = bits + bits_auxiliar.charAt(primero);
//                        guardado += 1;
//                    } else
//                    {
//                        bytes2[indice] =  (byte)Integer.parseInt((bits + bits_auxiliar.charAt(primero) ),2);
//                        indice++;
//                        bits = "";
//                        guardado = 0;
//                    }
//                    pos = (int) (pos + relleno + Math.ceil(aux_ancho/8));
//                }
//            }

            auxiliar = ancho;
            ancho = alto;
            alto = auxiliar;

            byte res[] = new byte[indice+62];

            System.arraycopy(bytes, 0, res, 0, 62);

            pos = 62;
            for (int p=0;p<indice;p++){
                res[pos] = bytes2[p];
                pos++;
            }

            //Cambio ancho con alto 
            byte ancho1, ancho2, ancho3, ancho4;
            ancho1 = res[18];
            ancho2 = res[19];
            ancho3 = res[20];
            ancho4 = res[21];

            res[18] = res[22];
            res[19] = res[23];
            res[20] = res[24];
            res[21] = res[25];

            res[22] = ancho1;
            res[23] = ancho2;
            res[24] = ancho3;
            res[25] = ancho4;

            bytes = new byte[indice+62];
            System.arraycopy(res, 0, bytes, 0, indice+62);


            try {
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(res));
                ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));

                InputStream in = new ByteArrayInputStream(res);
                imageActual = ImageIO.read(in);
            }
            catch (EOFException ex1) {
            }
            catch (IOException ex2) {
                System.err.println("An IOException was caught: " + ex2.getMessage());
            }
            
        }
        
        return imageActual;
    }
   
    public BufferedImage espejoH() throws IOException, InterruptedException
    {
        int pos, aux, ancho2, N, pos_derecha, aux2;
        boolean par;
        if (bytes[28] == 8)
        {
            pos = 1078;
            aux = pos;
            byte fila[] = new byte[ancho];
            for (int j=0;j<alto;j++) 
            {
                //guardo la fila
                for (int i = 0; i < ancho ; i++) {
                    fila[i] = bytes[pos];
                    pos++;
                }
                //Invierto la fila completa
                for (int i = 0; i < ancho / 2; i++) {
                    int temp = fila[i];
                    fila[i] = fila[ancho - 1 - i];
                    fila[ancho - 1 - i] = (byte) temp;
                }
                //almaceno la fila volteada en bytes
                for (int i = 0; i < ancho ; i++) {
                    bytes[aux] = fila[i];
                    aux++;
                }
            }
        } else if (bytes[28]==24)
        {
            relleno = relleno24bits();
            pos = 54;
            aux = pos;
            pos_derecha = pos + (ancho*3) - 1;
            byte R, G, B;
            
            if (ancho%2 == 0){
                ancho2 = ancho/2;
            } else{
                ancho2 = (ancho/2) + 1;
            }
            N = alto*ancho2;
            for (int i=0;i<N;i++)
            {
                if (relleno!=0 && pos==aux+((ancho*3)/2)){
                    pos += ((ancho*3)/2)+relleno;
                    pos_derecha = pos + (ancho*3)-1 + relleno;
                    aux = pos;
                } else if (relleno==0 && pos==aux+((ancho*3)/2)) {
                    pos += ((ancho*3)/2);
                    pos_derecha = pos + (ancho*3)-1;
                    aux = pos;
                }
                R = bytes[pos_derecha];
                G = bytes[pos_derecha-1];
                B = bytes[pos_derecha-2];
                bytes[pos_derecha] = bytes[pos+2];
                bytes[pos_derecha-1] = bytes[pos+1];
                bytes[pos_derecha-2] = bytes[pos];

                bytes[pos] = B;
                bytes[pos+1] = G;
                bytes[pos+2] = R;

                pos += 3;
                pos_derecha -= 3;
            }
           
        } else if (bytes[28]==4)
        {
            relleno = relleno4bits();
            String by, by_final;
            if (ancho%2 ==  0) {
                ancho2 = ancho/2;
                par = true;
            } else {
                ancho2 = (ancho/2)+1;
                par = false;
            }
            pos = 118;
            aux = 118;
            pos_derecha = pos + (ancho2) - 1;
            N = alto*(ancho2/2);
            
            if (par)
            {
                for (int i=0;i<N;i++) 
                {
                    if(pos == aux+(ancho2/2))
                    {
                        pos += (ancho2/2) + relleno;
                        pos_derecha = pos + ancho2 - 1;
                        aux = pos;
                    } else {
                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_derecha]&0xff))).replace(' ', '0');
                        
                        bytes[pos] = (byte)Integer.parseInt(by.substring(4,8)+by.substring(0,4),2);
                        bytes[pos_derecha] = (byte)Integer.parseInt(by_final.substring(4,8)+by_final.substring(0,4),2);
                        
                        aux2 = bytes[pos];
                        bytes[pos] = bytes[pos_derecha];
                        bytes[pos_derecha] = (byte) aux2;
                        
                        pos++;
                        pos_derecha--;
                        
                    }
                }

            } else 
            {
                String pri_cuatro, seg_cuatro, bits_aux="", by_anterior;
                
                for (int i=0;i<alto;i++){
                    for (int j=0;j<ancho2/2;j++){
                        if (pos == aux)
                        {
                            by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                            by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_derecha]&0xff))).replace(' ', '0');

                            pri_cuatro = by.substring(0, 4);
                            seg_cuatro = by_final.substring(0,4);
                            bits_aux = by.substring(4,8);

                            bytes[pos] =  (byte)Integer.parseInt((seg_cuatro + bits_aux),2);
                            bytes[pos_derecha] = (byte)Integer.parseInt((pri_cuatro + by_final.substring(4,8)),2);

                            pos++;
                            pos_derecha--;
                        } else
                        {
                            by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                            by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_derecha]&0xff))).replace(' ', '0');
                            by_anterior = String.format("%8s", Integer.toBinaryString(((int)bytes[pos-1]&0xff))).replace(' ', '0');

                            bytes[pos-1] = (byte)Integer.parseInt(by_anterior.substring(0,4)+by_final.substring(4,8),2);
                            bytes[pos_derecha] = (byte)Integer.parseInt(by.substring(0,4)+bits_aux,2);
                            bits_aux = by.substring(4,8);
                            bytes[pos] = (byte)Integer.parseInt(by_final.substring(0,4)+bits_aux,2);
                            pos++;
                            pos_derecha--;
                        }
                    }
                    pos += (ancho2/2) + relleno;
                    pos_derecha = pos + ancho2 - 1;
                    aux = pos;
                }
                
            }
                
        } else if (bytes[28]==1)
        {
            relleno = relleno1bits();
            String aux_bits = "";
            boolean impar;
            
            if (ancho%8 == 0)
            {
                ancho2 = ancho/8;
                impar = false;
            } else
            {
                ancho2 = (ancho/8)+1;
                impar = true;
            } 
            pos = 62;
            aux = 62;
            pos_derecha = pos + ancho2 - 1;
            int ancho_aux = ancho;
            String by, by_final,by_anterior, pri_cuatro, seg_cuatro, bits_aux="", cambio,cambio_final;
//            N = alto*(ancho2/2);
            while (ancho_aux > 8)
                ancho_aux -= 8;
            
          
            if (impar)
            {
                for (int i=0;i<alto;i++){
                    for (int j=0;j<ancho2/2;j++){
                        if (pos == aux)
                        {
                            by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                            by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_derecha]&0xff))).replace(' ', '0');

                            cambio = by.substring(0, ancho_aux);
                            cambio_final = by_final.substring(0,ancho_aux);

                            StringBuilder builder=new StringBuilder(cambio);
                            cambio = builder.reverse().toString();
                            builder=new StringBuilder(cambio_final);
                            cambio_final = builder.reverse().toString();
                            
                            bytes[pos] =  (byte)Integer.parseInt((cambio_final + by.substring(ancho_aux,8)),2);
                            bytes[pos_derecha] = (byte)Integer.parseInt((cambio + by_final.substring(ancho_aux,8)),2);
                            bits_aux = by.substring(ancho_aux,8);
                            
                            pos++;
                            pos_derecha--;
                        } else
                        {
                            by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                            by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_derecha]&0xff))).replace(' ', '0');
                            by_anterior = String.format("%8s", Integer.toBinaryString(((int)bytes[pos-1]&0xff))).replace(' ', '0');

                            cambio = by.substring(0, ancho_aux);
                            cambio_final = by_final.substring(ancho_aux,8);

                            StringBuilder builder=new StringBuilder(cambio);
                            cambio = builder.reverse().toString();
                            builder=new StringBuilder(cambio_final);
                            cambio_final = builder.reverse().toString();
                            builder=new StringBuilder(bits_aux);
                            bits_aux = builder.reverse().toString();

                            bytes[pos-1] = (byte)Integer.parseInt(by_anterior.substring(0,ancho_aux)+cambio_final,2);
                            bytes[pos_derecha] = (byte)Integer.parseInt(cambio+bits_aux,2);
                            bits_aux = by.substring(ancho_aux,8);
                            String x = by_final.substring(0,ancho_aux);
                            builder=new StringBuilder(x);
                            x = builder.reverse().toString();
                            bytes[pos] = (byte)Integer.parseInt(x+bits_aux,2);
                            pos++;
                            pos_derecha--;
                        }
                    }
                    pos += (ancho2/2) + relleno;
                    pos_derecha = pos + ancho2 - 1;
                    aux = pos;
                }
            } else
            {
                for (int i=0;i<alto;i++)
                {
                    for (int j=0;j<ancho2/2;j++)
                    {
                        by =  String.format("%8s", Integer.toBinaryString(((int)bytes[pos]&0xff))).replace(' ', '0');
                        by_final = String.format("%8s", Integer.toBinaryString(((int)bytes[pos_derecha]&0xff))).replace(' ', '0');
                        
                        StringBuilder builder = new StringBuilder(by);
                        by = builder.reverse().toString();
                        builder = new StringBuilder(by_final);
                        by_final = builder.reverse().toString();
                        bytes[pos] = (byte)Integer.parseInt(by_final,2);
                        bytes[pos_derecha] = (byte)Integer.parseInt(by,2);

                        pos++;
                        pos_derecha--;
                    }
                    pos += (ancho2/2) + relleno;
                    pos_derecha = pos + ancho2 - 1;
//                    aux = pos;
                    
                }
            }
        }

       
        
        InputStream in = new ByteArrayInputStream(bytes);
        imageActual = ImageIO.read(in);
        
        BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytes));
        ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
        

        return imageActual;
    }
    
    public BufferedImage espejoV() throws IOException, InterruptedException
    {
        int pos, aux, pos_up, N, aux2, ancho2;
        boolean par;
        byte R, G, B;
        if (bytes[28] == 24)
        {
            relleno = relleno24bits();
            pos = 54;
            aux = pos;
            pos_up = bytes.length-relleno-(ancho*3);
            par = (alto%2==0);
            
            if (par)
               N = ancho*(alto/2);
            else
               N = ancho*((alto/2)+1);
                        
            for (int i=0;i<N;i++)
            {
                if (relleno!=0 && pos==aux+(ancho*3)){
                    pos += relleno;
                    pos_up -= (2*ancho*3) - relleno;
                    aux = pos;
                }
                else if (relleno==0 && pos==aux+(ancho*3)){
                    pos_up -= (2*ancho*3);
                    aux = pos;
                }
                R = bytes[pos_up+2];
                G = bytes[pos_up+1];
                B = bytes[pos_up];
                
                bytes[pos_up+2] = bytes[pos+2];
                bytes[pos_up+1] = bytes[pos+1];
                bytes[pos_up] = bytes[pos];
                
                bytes[pos] = B;
                bytes[pos+1] = G;
                bytes[pos+2] = R;
                
                pos += 3;
                pos_up += 3;
            }

        } else if (bytes[28]==8)
        {
            relleno = relleno8bits();
            pos = 1078;
            aux = pos;
            pos_up = bytes.length-relleno-ancho;
            par = (alto%2==0);
            
            if (par)
               N = ancho*(alto/2);
            else
               N = ancho*((alto/2)+1);
            
            for (int i=0;i<N;i++)
            {
                if (relleno!=0 && pos==aux+ancho){
                    pos += relleno;
                    pos_up -= (2*ancho) - relleno;
                    aux = pos;
                }
                else if (relleno==0 && pos==aux+ancho){
                    pos_up -= (2*ancho);
                    aux = pos;
                }
                aux2 = bytes[pos_up];
                bytes[pos_up] = bytes[pos];
                bytes[pos] = (byte)aux2;
                pos++;
                pos_up++;
                
            }
            
        } else if (bytes[28]==4)
        {
            relleno = relleno4bits();            
            int limite;
            boolean impar;
            if (alto%2 ==  0)
                limite = 0;
            else 
                limite = 1;
           
            if (ancho%2 ==  0){
                impar  = false;
            } else {
                impar = true;
            }
            
            pos = 118;
            pos_up = (int) (bytes.length-(Math.ceil(ancho/2))-relleno);
            aux = pos_up;
            N = (alto/2) + limite;
            
            if (impar)
            {
                for (int i=0;i<N;i++)
                {
                    byte temp[] = new byte[ancho/2];
                    System.arraycopy(bytes, pos, temp, 0, ancho/2);
                    System.arraycopy(bytes, aux, bytes, pos, ancho/2);
                    System.arraycopy(temp, 0, bytes, aux, ancho/2);
                    
                    pos = pos + (ancho/2) + relleno + 1;
                    aux = aux - (ancho/2) - relleno - 1;
                }
            } else
            {
                
                if (alto%2 ==  0)
                    limite = alto/2;
                else 
                    limite = (alto/2)+1;
                if (ancho%2 ==  0){
                    ancho2 = (ancho/2);
                } else {
                    ancho2 = ((ancho/2)+1);
                }
                
                N = limite*ancho2;
                pos = 118;
                aux = 118;
                pos_up = (int) (bytes.length-(Math.ceil(ancho/2))-relleno);
                for (int i=0;i<N;i++)
                {
                    if(pos == aux+ancho2 && relleno!=0) {
                        pos+=relleno;
                        pos_up -= (2*ancho2) - relleno;
                        aux = pos;
                    } else if (relleno==0 && pos==aux+ancho2) {
                        pos_up -= (2*ancho2);
                        aux = pos;
                    } 
                    aux2 = bytes[pos];
                    bytes[pos] = bytes[pos_up];
                    bytes[pos_up] = (byte) aux2;
                    pos++;
                    pos_up++;
                }
            }     
            
            
        } else if (bytes[28]==1)
        {
            int limite, impar;
            relleno = relleno1bits();
            if (alto%2 ==  0)
                limite = alto/2;
            else 
                limite = (alto/2)+1;
            
            if (ancho%8 == 0) {
                ancho2 = ancho/8;
                impar = 1;
            } else {
                ancho2 = (ancho/8)+1;
                impar = 0;
            }
                
            float ancho_aux = ancho;
            N = limite*ancho2;
            pos = 62;
            aux = 62;
            pos_up = (int) (bytes.length-(Math.ceil(ancho_aux/8))-relleno);
            int aux_up = pos_up;
            
            for (int i=0;i<N;i++)
            {
                if (pos == aux+(Math.floor(ancho_aux/8))- impar  )
                {
                    aux2 = bytes[pos];
                    bytes[pos] = bytes[aux_up];
                    bytes[aux_up] = (byte) aux2;
                    
                    pos += relleno + 1;
                    pos_up = (int) (pos_up - relleno - (Math.ceil(ancho_aux/8)));
                    aux_up = pos_up;
                    aux = pos;
                    
                } else
                {
                    aux2 = bytes[pos];
                    bytes[pos] = bytes[aux_up];
                    bytes[aux_up] = (byte)aux2;
                    pos++;
                    aux_up++;
                }
                
                    
            }
            
        }
        
        InputStream in = new ByteArrayInputStream(bytes);
        imageActual = ImageIO.read(in);
        
        BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytes));
        ImageIO.write(imag, "bmp", new File(dirName,"salida.bmp"));
        

        return imageActual;
    }
    
    public int relleno1bits()
    {
        float ancho2, aux_relleno;
        
        if (ancho%8 == 0)
        {   
            
            ancho2 = (float)ancho/8;
            aux_relleno = (int)ancho2/4;
            aux_relleno = (ancho2/4) - aux_relleno;
            if (aux_relleno == 0.5)
                return 2;
            else if (aux_relleno == 0.25)
                return 3;
            else if (aux_relleno == 0.75)
                return 1;
            else
                return 0;
        } else
        {
            ancho2 = (float)(ancho/8)+1;
            aux_relleno = (int)ancho2/4;

            aux_relleno = (ancho2/4) - aux_relleno;
            if (aux_relleno == 0.5)
                return 2;
            else if (aux_relleno == 0.25)
                return 3;
            else if (aux_relleno == 0.75)
                return 1;
            else
                return 0;
        } 
    }
    
    public int relleno4bits()
    {
        if (ancho%2 ==  0)
        {
            float ancho2 = (float)ancho/2;
            float aux_relleno = (int)ancho2/4;
            aux_relleno = (ancho2/4) - aux_relleno;
            
            if (aux_relleno == 0.5)
                return relleno = 2;
            else if (aux_relleno == 0.25)
                return relleno = 3;
            else if (aux_relleno == 0.75)
                return relleno = 1;
            else
                return relleno = 0;
        } else
        {
            float ancho2 = (float)((ancho/2)+1);
            float aux_relleno = (int)ancho2/4;
            aux_relleno = (ancho2/4) - aux_relleno;
            
            if (aux_relleno == 0.5)
                return relleno = 2;
            else if (aux_relleno == 0.25)
                return relleno = 3;
            else if (aux_relleno == 0.75)
                return relleno = 1;
            else
                return relleno = 0;
        }
    }
    
    public int relleno8bits() 
    {
        float aux_relleno = ancho/4;
        aux_relleno = (float)ancho/4 - aux_relleno;
        
        if (aux_relleno == 0.5)
            return 2;
        else if (aux_relleno == 0.25)
            return 3;
        else if (aux_relleno == 0.75){
            return 1;
        }
        else
            return 0;
    }
    
    
    public int relleno24bits() 
    {
        float aux_relleno = (ancho*3)/4;
        aux_relleno = (float)(ancho*3)/4 - aux_relleno;
        
        if (aux_relleno == 0.5)
            return 2;
        else if (aux_relleno == 0.25)
            return 3;
        else if (aux_relleno == 0.75){
            return 1;
        }
        else
            return 0;
    }
    
    
}

////////////////////////VER ESTOO/////////////////////////////////////

//Error espejo vertical de 8 bits con pastornubes
//CAMBIAR ruta de guardado

////////////////////VER Esto////////////////////////////////////////