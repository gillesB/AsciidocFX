package com.kodedu.service.extension.chart;

import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.springframework.stereotype.Component;

@Name("chart")                                              
@Contexts({Contexts.OPEN})                            
@ContentModel(ContentModel.EMPTY)
@Component
public class FxChartBlockProcessor extends BlockProcessor {

    private ChartProvider chartProvider;
    
    public FxChartBlockProcessor(ChartProvider chartProvider) {
    	this.chartProvider = chartProvider;
	}

	@Override
	public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
		String imagesDir = String.valueOf(parent.getDocument().getAttribute("imagesdir"));
		String chartContent = reader.read();
		String chartType = String.valueOf(attributes.get("2"));
		String imageTarget = String.valueOf(attributes.get("file"));
		var optMap = parseChartOptions(String.valueOf(attributes.get("opt")));
		
		chartProvider.getProvider(chartType).chartBuild(chartContent, imagesDir, imageTarget, optMap);
		// Not sure why, but it seems that it only works if target is set afterwards
		// https://stackoverflow.com/questions/71595454/create-image-block-with-asciidocj/71849631#71849631
		Block block = createBlock(parent, "image", Collections.emptyMap());
		block.setAttribute("target", imageTarget, true);
		return block;
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
