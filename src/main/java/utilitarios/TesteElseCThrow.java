package weiEtal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;

import utilitarios.Leitor;
import utilitarios.Padrao;

/**
 * Classe para testar a refatoração com métodos reflexivos
 * @author Thyyago Henrique Pacher
 */
public class App{

    private App instance;
    private String valor;

    public App() {

    }

    private App(String valor) {

    }

    private void metodo1() {
        boolean res = false;
        System.out.println(" -- verificação do método 01 --");
        instance = null;
        if (res == true) {
            res = false;
        }
        if (instance != null) {
            instance = new App();
        }
        if (instance == null) {
            instance = new App();
        }else {
        	throw new IllegalStateException("Erro causado por: classe está nula");
        }
    }

    private boolean metodo2(boolean res) {
        return true;
    }

    /** exemplo para carregar classes de outro diretório em java ele pega só arquivos .class */
    public void carregaClasseOutraURL() {
        try {
            File file = new File("c:\\other_classes\\");
            //convert the file to URL format
            URL url = file.toURI().toURL();
            URL[] urls = new URL[] { url };
            //load this folder into Class loader
            ClassLoader cl = new URLClassLoader(urls);
            //load the Address class in 'c:\\other_classes\\'
            Class cls = cl.loadClass("com.mkyong.io.Address");
            //print the location from where this class was loaded
            ProtectionDomain pDomain = cls.getProtectionDomain();
            CodeSource cSource = pDomain.getCodeSource();
            URL urlfrom = cSource.getLocation();
            System.out.println(urlfrom.getFile());

            System.out.println("Nome da classe carregada: " + cls.getSimpleName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void verificarProjetoExemplo() {
//        Refatoracao rf = new Refatoracao();
//        rf.lerProjeto("C:\\programa-java\\exemplo-cavado-Liu-factory");
        
    	gaitaniEtal.Refatoracao rf = new gaitaniEtal.Refatoracao();
        rf.lerProjeto("C:\\programa-java\\exemplo-cavada-gaitani-nullobject");        
    }


	//use ASTParse to parse string
	public static void parse(String str) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
 
		cu.accept(new ASTVisitor() {
 
			Set names = new HashSet();
 
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out.println("Declaration of '" + name + "' at line"
						+ cu.getLineNumber(name.getStartPosition()));
				return false; // do not continue 
			}
 
			public boolean visit(SimpleName node) {
				if (this.names.contains(node.getIdentifier())) {
					System.out.println("Usage of '" + node + "' at line "
							+ cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}
		});
 
	}    
   
	//read file content into a string
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
 
		return  fileData.toString();	
	}	
	
    public static void main(String[] args) {
    	try {
    		App app = new App();
    		
			Leitor leitor = new Leitor("App", "C:\\programa-java\\MetodoLiu\\src\\main\\java\\weiEtal\\App.java");
			List<com.github.javaparser.ast.stmt.Statement> linhas = leitor.linhasMetodo("metodo1");
			for (com.github.javaparser.ast.stmt.Statement statement : linhas) {
				if(statement instanceof IfStmt) {
					System.out.println(statement);
					IfStmt ifStmt = (IfStmt) statement;
					if(ifStmt.hasElseBlock()) {
						System.out.println("Bloco else: " + ifStmt.getElseStmt().get());
						
						System.out.println(ifStmt.getElseStmt().get().isThrowStmt());
						System.out.println(ifStmt.getElseStmt().get() instanceof ThrowStmt);
						
						List<?> nosElse = ifStmt.getElseStmt().get().getChildNodes();
						
						//pegando lin por lin dentro do else.
						for (Object object : nosElse) {
							System.out.println(object instanceof ThrowStmt);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
    }
}
