package br.com.caelum.vraptor.cdi.agent;

import br.com.caelum.vraptor.Resource;

@Resource
public class SomeController{
	
	private final String parameter;

	public SomeController(String parameter){
		this.parameter = parameter;
		
	}
}