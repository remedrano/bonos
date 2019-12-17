package com.vabono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Hello world!
 *
 */
public class App 
{
	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String FILENAME = "nodoenlace.xlsx";
	
	public static double contadorGeneral = 0;
	
	public static int granTotal2018 = 0;
	public static int granTotal2019 = 0;
	
	
    public static void main( String[] args ) throws EncryptedDocumentException, InvalidFormatException, IOException
    {
    	generarJsonNodeEnlace();
    }
    
    public static void generarJsonNodeEnlace() throws EncryptedDocumentException, InvalidFormatException, IOException {
    	
    	File currentDirFile = new File("..");    
    	String ruta = Paths.get( currentDirFile.getCanonicalPath() ).getParent().toString();    	

        Workbook workbook = WorkbookFactory.create(new File( ruta +fileSeparator+"dataset"+ fileSeparator +FILENAME));
        System.out.println( "Inicio de generación de json!" );
        
        Sheet sheet = workbook.getSheetAt(0);        
        DataFormatter dataFormatter = new DataFormatter();
        
        JSONObject dataJson = new JSONObject();
     
                    
        List<Pais> paises = new ArrayList<Pais>();
                       
        		       
        
        sheet.forEach( row ->  {
   
        	if( row.getRowNum() > 0) {
        		        		
	        	String nombrePais = dataFormatter.formatCellValue(row.getCell(2)).toString();	  
	        		        	
	        	Optional<Pais> paisOptional = paises.stream().filter(p -> p.getNombre().equals( nombrePais )).findFirst();
	        	
	        	Pais paisFind = null;
	        	//System.out.println("departamentoOptional-->"+departamentoOptional.isPresent());
	        	//System.out.println("nombreCelda-->"+nombreCelda);
	        	if( paisOptional.isPresent()  ) {        	
	        		paisFind = paisOptional.get();
	        	}
	        		        		        
	        	if( paisFind != null) {
	        
	        		List<Anio> aniosPais = paisFind.getAnios();
		        			        		        	
	    			int anio = Integer.parseInt( dataFormatter.formatCellValue(row.getCell(0)).toString()) ;
	            	Optional<Anio> anioOptional = aniosPais.stream().filter(p -> p.getAnio() == anio ).findFirst();
	            	
	            	Anio anioFind = null;
	            	
	            	if(anioOptional.isPresent()) {	  	            		
	            		anioFind = anioOptional.get();	
	            		int valorActual = anioFind.getTotal();
	            		int valorCelda = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(3)).toString());
	            		if( anioFind.getAnio() == 2018) granTotal2018 += valorCelda;
	            		if( anioFind.getAnio() == 2019) granTotal2019 += valorCelda;
	            		
	            		anioFind.setTotal(  valorActual + valorCelda);
	            	} 
	  
	            	if( anioFind == null) {
	            		anioFind = new Anio( Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)).toString()) ,Integer.parseInt(dataFormatter.formatCellValue(row.getCell(3)).toString()), 0);
	            		if( anioFind.getAnio() == 2018) granTotal2018 += anioFind.getTotal();
	            		if( anioFind.getAnio() == 2019) granTotal2019 += anioFind.getTotal();	            		
	            		aniosPais.add( anioFind);
	            	}	            	
	            	
	            		
	        	}else {	        	
	        		        
	        		Anio anioFind = new Anio( Integer.parseInt( dataFormatter.formatCellValue(row.getCell(0)).toString() ),Integer.parseInt(dataFormatter.formatCellValue(row.getCell(3)).toString()), 0);
	        		
	        		if( anioFind.getAnio() == 2018) granTotal2018 += anioFind.getTotal();
            		if( anioFind.getAnio() == 2019) granTotal2019 += anioFind.getTotal();           
	        		List<Anio> aniosPais = new ArrayList<Anio>();
	        		aniosPais.add(anioFind );
	        		
	        		paisFind = new Pais( row.getCell(2).toString() , aniosPais);
	        		paises.add( paisFind );
	        	}
	        		        	
        	}                  
        });
        
        
       System.out.println("Cantidad total-->"+granTotal2018 + " -- "+granTotal2019);
        
       JSONArray nodes = new JSONArray();
       JSONArray links = new JSONArray();
       
       DecimalFormat formato = new DecimalFormat("#.00000");

               
        paises.forEach(pais->{  
        
        	pais.getAnios().forEach(anio->{         		
        		JSONObject paisesNode = new JSONObject();
        		paisesNode.put("id", pais.getNombre()+"_"+anio.getAnio());
        		paisesNode.put("pais", pais.getNombre() );
        		paisesNode.put("cantidad", anio.getTotal());
        		System.out.println("anio.getTotal() -->"+( anio.getTotal() * 100 / granTotal2018  ) );
        		
        		double valor2018 = ( anio.getTotal() * 100 / granTotal2018  );
        		double valor2019 = ( anio.getTotal() * 100 / granTotal2019 );
        		if( anio.getAnio() == 2018) {
        			paisesNode.put("porcentaje", ( formato.format(valor2018) ) +"%");	
        		}
    			if( anio.getAnio() == 2019) {
    				paisesNode.put("porcentaje", (formato.format(valor2019) ) +"%");
        		}
        		        		
            	nodes.add(paisesNode );
            	
            	JSONObject paisesLink = new JSONObject();
            	            	
            	paisesLink.put("source", pais.getNombre()+"_"+anio.getAnio());
            	paisesLink.put("target", "Colombia" );
            	paisesLink.put("cantidad", anio.getTotal());
            	if( anio.getAnio() == 2018) {
            		paisesLink.put("porcentaje",(valor2018)+"%");            		
            	}
            	if( anio.getAnio() == 2019) {
            		paisesLink.put("porcentaje",(valor2019+"%"));
            		
            	}
            	
            	links.add(paisesLink );
            	
        	});         	  
        });
        
        //Agregar colombia
        JSONObject paisesNode = new JSONObject();
		paisesNode.put("id", "Colombia");
		paisesNode.put("pais", "Colombia" );
		paisesNode.put("cantidad", "1000000");
		paisesNode.put("porcentaje","100%");
    	nodes.add(paisesNode );
        
        dataJson.put("nodes", nodes);
        dataJson.put("links", links);
        
        System.out.println( dataJson );
    
		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter(ruta + fileSeparator +"dataset"+fileSeparator+"nodeenlace.json")) {
			file.write(dataJson.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			//System.out.println("\nJSON Object: " + dataJson);
		}

        // Closing the workbook
        workbook.close();

    	
    }
}
