package com.kodedu.service.extension.chart;

import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;

@Name("foo")                                              
@Contexts({Contexts.OPEN})                            
@ContentModel(ContentModel.SIMPLE)
public class FooBlockProcessor extends BlockProcessor {

	@Override
	public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
		var options = new HashMap<Object, Object>();
    	options.put("source", null);
    	options.put("target", "C:/Temp/PNG_transparency_demonstration_1.png");
    	Block block = this.createBlock(parent, "image", options);
    	return block;
	}

}
