package com.kodedu.service.extension.chart;

import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Name("chart")                                              
@Contexts({Contexts.OPEN})                            
@ContentModel(ContentModel.SIMPLE)
@Component
public class FxChartBlockProcessor extends BlockProcessor {

    @Autowired
    private ChartProvider chartProvider;

	@Override
	public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
		String imagesDir = String.valueOf(parent.getDocument().getAttribute("imagesdir"));
		String chartContent = reader.read();
		String chartType = String.valueOf(attributes.get("2"));
		String imageTarget = String.valueOf(attributes.get("file"));
		var optMap = parseChartOptions(String.valueOf(attributes.get("opt")));
		
		chartProvider.getProvider(chartType).chartBuild(chartContent, imagesDir, imageTarget, optMap);
		Block block = createImageBlock(parent, attributes);
		return null;
	}
	
    private Block createImageBlock(StructuralNode parent, Map<String, Object> attributes) {
    	var options = new HashMap<Object, Object>();
    	options.put("source", null);
    	options.put("target", attributes.get("file"));
    	return this.createBlock(parent, "image", options);
	}

	private Map<String, String> parseChartOptions(String options) {
        Map<String, String> optMap = new HashMap<>();
        if (nonNull(options)) {
            String[] optPart = options.split(",");

            for (String opt : optPart) {
                String[] keyVal = opt.split("=");
                if (keyVal.length != 2) {
                    continue;
                }
                optMap.put(keyVal[0], keyVal[1]);
            }
        }
        return optMap;
    }

}
