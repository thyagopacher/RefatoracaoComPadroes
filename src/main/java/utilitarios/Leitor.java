package utilitarios;

import java.io.File;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

/**
 * Leitor
 */
public class Leitor {

	private String pacoteClasse = "";
	private String nomeClasse;
	private CompilationUnit cu = null;
	private String tipoClasse = "";
	private String caminhoClasse;
	private File arquivoClasse;
	public boolean erro = false;

	public Leitor() {

	}

	/**
	 * @param nomeClasse
	 *            - como a classe se chama
	 * @param caminho
	 *            - aonde a classe se encontra
	 */
	public Leitor(String nomeClasse, String caminho) {
		try  {
			this.caminhoClasse = caminho;
			this.nomeClasse = nomeClasse;
			this.arquivoClasse = new File(this.caminhoClasse);
			cu = JavaParser.parse(arquivoClasse);
		} catch (Exception ex) {
			this.erro = true;
			System.out.println("Erro causado por: " + ex.getMessage() + " no caminho: " + caminho);
		}
	}

	
	/**
	 * retorna os métodos da classe que estiver sendo lida
	 * 
	 * @return List<MethodDeclaration>
	 * @throws ex
	 *             - excessão caso não consiga ler as linhas
	 */
	public List<MethodDeclaration> metodosDeclaradosJavaParser() {
		return this.getClassOrInterfaceDeclaration().getMethods();
	}

	/**
	 * retorna as linhas do método de maneira legivel
	 * 
	 * @return List<Statement>
	 */
	public List<Statement> linhasMetodo(String nomeMetodo) {
		return this.getClassOrInterfaceDeclaration().getMethodsByName(nomeMetodo).get(0).getBody().get().getStatements();
	}

	/**
	 * retorna a primeira classe do arquivo
	 * */
	public ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration() {
		return (ClassOrInterfaceDeclaration) this.getCu().getType(0);
	}	
	
	/**
	 * retorna os campos declarados na classe
	 * 
	 * @return List<FieldDeclaration>
	 */
	public List<FieldDeclaration> camposClasse() {
		return this.getClassOrInterfaceDeclaration().getFields();
	}

	/**
	 * retorna o if que tiver new na condição
	 */
	public IfStmt temNewNoIf(IfStmt ifStmt) {
		for (FieldDeclaration fieldDeclaration : this.camposClasse()) {
			String campo = fieldDeclaration.getVariable(0).getNameAsString();
			if (ifStmt.getCondition().toString().contains(campo)) {
				/** achou um parametro sendo usado no condicional */
				return ifStmt;
			}
		}
		return null;
	}

	/**
	 * verifica se tem parametros no condicional IF
	 * 
	 * @param parametrosMetodo
	 *            - os parametros do método
	 * @param condicional
	 *            - condicional perante o IF
	 * @return retorna o condicional cujo houver parametro sendo usado nele.
	 */
	public IfStmt temParametroNoIf1(NodeList<Parameter> parametrosMetodo,
			IfStmt ifStmt) {
		for (Parameter parametro : parametrosMetodo) {
			String condicao = ifStmt.getCondition().toString();
			String thenIf = ifStmt.getThenStmt().toString();
			if (!thenIf.contains("new ") && condicao.contains(parametro.getName().toString())) {
				/** achou um parametro sendo usado no condicional */
				return ifStmt;
			}
		}
		return null;
	}

	public IfStmt temParametroNoIf2(NodeList<Parameter> parametrosMetodo,
			IfStmt ifStmt) {
		for (Parameter parametro : parametrosMetodo) {
			String condicao = ifStmt.getCondition().toString();
			String thenIf = ifStmt.getThenStmt().toString();
			if (thenIf.contains("new ") && condicao.contains(parametro.getName().toString())) {
				/** achou um parametro sendo usado no condicional */
				return ifStmt;
			}
		}
		return null;
	}

	/**
	 * retorna qual caminho da pasta aonde estiver a classe
	 * 
	 * @return the caminhoClasse
	 */
	public String getCaminhoClasse() {
		return caminhoClasse;
	}

	/**
	 * se é interface, abstract, ou class
	 * 
	 * @return the tipoClasse
	 */
	public String getTipoClasse() {
		if(tipoClasse.isEmpty()) {
			if(this.getClassOrInterfaceDeclaration().isAbstract()) {
				tipoClasse = "abstract";
			}else if(this.getClassOrInterfaceDeclaration().isInterface()) {
				tipoClasse = "interface";
			}else {
				tipoClasse = "class";
			}
		}
		return tipoClasse;
	}

	/**
	 * @param tipoClasse
	 *            the tipoClasse to set
	 */
	public void setTipoClasse(String tipoClasse) {
		this.tipoClasse = tipoClasse;
	}

	/**
	 * @return the erro
	 */
	public boolean isErro() {
		return erro;
	}

	/**
	 * @param erro
	 *            the erro to set
	 */
	public void setErro(boolean erro) {
		this.erro = erro;
	}

	/**
	 * @param caminhoClasse
	 *            the caminhoClasse to set
	 */
	public void setCaminhoClasse(String caminhoClasse) {
		this.caminhoClasse = caminhoClasse;
	}

	/**
	 * retorna a CompilationUnit para usar com javaparser.
	 */
	public CompilationUnit getCu() {
		return cu;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	/*
	 * nome do pacote
	 */
	public String getPacoteClasse() {
		if(pacoteClasse.isEmpty()) {
			pacoteClasse = this.getCu().getPackageDeclaration().toString();
		}		
		return pacoteClasse;
	}

	public void setPacoteClasse(String pacoteClasse) {
		this.pacoteClasse = pacoteClasse;
	}

	/**
	 * retorna o file do arquivo ao qual leu a classe
	 */
	public File getArquivoClasse() {
		return arquivoClasse;
	}

	public void setArquivoClasse(File arquivoClasse) {
		this.arquivoClasse = arquivoClasse;
	}

	/**
	 * nome da classe qual estiver fazendo a leitura
	 * 
	 * @return the nomeClasse
	 */
	public String getNomeClasse() {
		return nomeClasse;
	}

	/**
	 * @param nomeClasse
	 *            the nomeClasse to set
	 */
	public void setNomeClasse(String nomeClasse) {
		this.nomeClasse = nomeClasse;
	}

}