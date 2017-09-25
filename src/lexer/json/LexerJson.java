/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer.json;

import com.sun.org.apache.xerces.internal.impl.dv.xs.TypeValidator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author Lore
 */
public class LexerJson {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      String ubicacionArchivo= "D:\\LCIK-\\6to semestre y 7mo semestre\\Compiladores\\tarea_1\\fuente.txt";
      File archivo = null;
      FileReader fr = null;
      BufferedReader br = null;

      try {
         // Apertura del fichero y creacion de BufferedReader para poder
         // hacer una lectura comoda (disponer del metodo readLine()).
         archivo = new File (ubicacionArchivo);
         fr = new FileReader (archivo);
         br = new BufferedReader(fr);
         String resultado = "";
         // Lectura del fichero
         String linea;
       while((linea=br.readLine())!= null){
          //  System.out.println(linea);
            resultado = resultado +'\n'+ analizarLinea( linea);
         }
         
         System.out.print(resultado + '\n');
          String ruta="D:\\output.txt";
          
         BufferedWriter bw;
         File archivo2 = new File(ruta);
         if(archivo.exists()) {
                bw = new BufferedWriter(new FileWriter(archivo2));
                bw.write("*::Lorena Maria Acosta - Jhony Alfredo Benitez.::*" + resultado);
          } else {
                bw = new BufferedWriter(new FileWriter(archivo2));
               bw.write("*::Lorena Maria Acosta - Jhony Alfredo Benitez. Se creo el archivo::*" + resultado);
          }
          bw.close();
      }
      catch(Exception e){
         e.printStackTrace();
      }finally{
         // En el finally cerramos el fichero, para asegurarnos
         // que se cierra tanto si todo va bien como si salta 
         // una excepcion.
         try{                    
            if( null != fr ){   
               fr.close();     
            }                  
         }catch (Exception e2){ 
            e2.printStackTrace();
         }
      }

    }
    public static String analizarLinea( String linea){
       String[] lexemas = new String[14];
       String[] compLexico = new String[12];
       lexemas[0]="["; compLexico[0]="L_CORCHETE ";
       lexemas[1]="]"; compLexico[1]="R_CORCHETE ";
       lexemas[2]="{"; compLexico[2]="L_LLAVE ";   
       lexemas[3]="}"; compLexico[3]="R_LLAVE ";   
       lexemas[4]=","; compLexico[4]="COMA ";   
       lexemas[5]="string"; compLexico[5]="LITERAL_CADENA ";   
       lexemas[6]="number"; compLexico[6]="LITERAL_NUMBER ";   
       lexemas[7]="true"; compLexico[7]="PR_TRUE ";   
       lexemas[8]="false"; compLexico[8]="PR_FALSE ";
       lexemas[9]="null"; compLexico[9]="PR_NULL ";   
       lexemas[10]=":"; compLexico[10]="DOS_PUNTOS ";   
       lexemas[11]="TRUE";   
       lexemas[12]="FALSE"; 
       lexemas[13]="NULL"; 
        String datos= " ";
        compLexico[11] ="ERROR_LEXICO ";
        boolean encLex = false;
        int index = 0;
        while (index < linea.length() && linea.charAt(index)!='/' ){
          char c= linea.charAt(index);
         
          for (int i=0; i< lexemas.length ; i++){
              if (lexemas[i].equals(Character.toString(c)) ){
                 datos = datos + compLexico[i] ;
                 encLex = true;
                 break;
              }
           }
          TokenValidator val=null;
           if (encLex == false){
               if (c == '"'){
                 val = sigTokenString(index ,linea);
                if(val.isValido()){
                    datos+=compLexico[5];
                }else{
                    datos+=compLexico[11];
                    break;
                }
                index = val.getPos();
               }else if(TypeValidator.isDigit(c)){
                 val=sigTokenNumber(index, linea);
                 if(val.isValido())
                    datos+=compLexico[6];
                 else{
                    datos+=compLexico[11];
                    break;
                 }
                 
                index = val.getPos();
                
               }else if (c == 't'||c == 'T'){
                   val  = sigTokenGeneric (index, linea, lexemas[7],lexemas[11]);
                   if (val.isValido()){
                       datos+=compLexico[7];
                   }else{
                       datos+=compLexico[11];
                       break;
                   }
                   index = val.pos;
      
                }else if (c == 'f'||c == 'F'){
                    val =sigTokenGeneric(index, linea, lexemas[8],lexemas[12]);
                    if(val.isValido()){
                        datos+=compLexico[8];
                    }else{
                        datos+=compLexico[11];
                        break;
                    }
                    index=val.pos;

                }else if (c == 'n'||c == 'N'){
                    val=sigTokenGeneric(index, linea, lexemas[9], lexemas[13]);
                    if ( val.isValido() ){
                        datos+=compLexico[9];
                    }else{
                        datos+=compLexico[11];
                     break;
                    }
                    index=val.getPos();
                }else if (c ==' '){
                    datos += " ";
                }else {
                datos+=compLexico[11];
                break;
                }
           }
           encLex = false;
           index = index+1;
           }
       return datos;
    }      
            
    
    public static TokenValidator sigTokenString( int inicio, String linea){
        TokenValidator n = new TokenValidator();
        
        int pos = inicio+1;
        char c = linea.charAt(pos);
        while (c != '"'&& pos<linea.length() ){
            c = linea.charAt(pos);
            pos++;

        }
        
        n.setPos(pos-1);
        n.setValido(true);
        if(linea.charAt(pos-1) != '"'){
            n.setValido(false);
        }
        return n;  
    }
    
    public  static TokenValidator sigTokenNumber( int inicio, String linea){
       
        TokenValidator tok = new TokenValidator();
        
        char c = linea.charAt(inicio);
        boolean coma = false;
        boolean eChar = false;
        
        while ( (TypeValidator.isDigit(c)||
                "e".equals(linea.charAt(inicio)+"") || 
                "E".equals(linea.charAt(inicio)+"")||
                ".".equals(linea.charAt(inicio)+""))&& inicio < linea.length() && c!=','){
            
              if("e".equals(linea.charAt(inicio)+"") ||"E".equals(linea.charAt(inicio)+"") ){
                  int pos = validar_exponencial(inicio, linea);
                  tok.setPos(pos-1);
                    tok.setValido(false);
                    if(pos!=-1){
                        tok.setValido(true);
                        return tok;
                    }else{
                        return tok;
                       }
              }
              
             if(".".equals(linea.charAt(inicio)+"")){
                int pos =  validar_decimal(inicio+1, linea);
                tok.setPos(pos-1);
                tok.setValido(false);
                if(pos!=-1){
                    tok.setValido(true);
                    return tok;
                }else{
                    return tok;
                   }
              }
             
            
            inicio++;
            if (inicio == linea.length()){
                tok.setPos(inicio-1);
                tok.setValido(true);
                return tok;
            }
            c = linea.charAt(inicio);
        }
        
        tok.setPos(inicio-1);
        tok.setValido(false);
        if( inicio == linea.length()-1){
            tok.setValido(true);
        };
        return tok;
    }

    private static TokenValidator sigTokenGeneric(int index, String linea, String lexema,String lexemaN) {
        TokenValidator tok= new TokenValidator();
        String cadena = ""; 
        char c = linea.charAt(index);
        while( c!= ' ' && c!='\n' && index<linea.length() && c!=','){
            cadena+=c;
              index++;
            if(index<linea.length())  
            c = linea.charAt(index);
         
        }
        tok.setPos(index-1);
         tok.setValido(lexema.equals(cadena)||lexemaN.equals(cadena));
         return tok;
    }

    private static int validar_decimal(int inicio, String linea) {
        char c = linea.charAt(inicio);
        
        if(!TypeValidator.isDigit(c)){
                return -1;
        }
        while(TypeValidator.isDigit(c)||
                "e".equals(linea.charAt(inicio)+"") || 
                "E".equals(linea.charAt(inicio)+"") && inicio < linea.length()){
            
            if ("e".equals(linea.charAt(inicio)+"") || 
                "E".equals(linea.charAt(inicio)+"")){
               int pos = validar_exponencial( inicio,  linea);
               return pos;
            }
            inicio++;
            if (inicio==linea.length()){
                break;
            }
            c = linea.charAt(inicio);
        }
        return inicio;
    }

    private static int validar_exponencial(int inicio, String linea){
        int val = inicio+1;
        char c = linea.charAt(val);
        if (c=='+' || c=='-'){
            val+=1;
            c = linea.charAt(val);
        }
        if(!TypeValidator.isDigit(c)){
            return -1;
        }
        while(TypeValidator.isDigit(c) && val < linea.length()){
            val= val +1;
            if (val==linea.length()){
                break;
            }
            c = linea.charAt(val);
        }
        return val;
    }
  
}
