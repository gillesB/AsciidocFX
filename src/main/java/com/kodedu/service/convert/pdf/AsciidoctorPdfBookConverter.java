package com.kodedu.service.convert.pdf;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kodedu.config.PdfConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
import com.kodedu.service.extension.chart.FooBlockProcessor;
import com.kodedu.service.extension.chart.FxChartBlockProcessor;
import com.kodedu.service.ui.IndikatorService;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class AsciidoctorPdfBookConverter implements DocumentConverter<String> {

    private final Logger logger = LoggerFactory.getLogger(AsciidoctorPdfBookConverter.class);

    private final ApplicationController asciiDocController;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
	private final PdfConfigBean pdfConfigBean;
	private final FxChartBlockProcessor fxChartBlockProcessor;

    @Autowired
    public AsciidoctorPdfBookConverter(final ApplicationController asciiDocController,
                            final IndikatorService indikatorService, final PdfConfigBean pdfConfigBean,
                            final ThreadService threadService, final DirectoryService directoryService, final Current current,
                            final FxChartBlockProcessor fxChartBlockProcessor) {
        this.asciiDocController = asciiDocController;
        this.indikatorService = indikatorService;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.current = current;
        this.pdfConfigBean = pdfConfigBean;
        this.fxChartBlockProcessor = fxChartBlockProcessor;
    }


    @Override
	public void convert(boolean askPath, Consumer<String>... nextStep) {

		String asciidoc = current.currentEditorValue();

		threadService.runTaskLater(() -> {

			final Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);

			File destFile = pdfPath.toFile();

			indikatorService.startProgressBar();
			logger.debug("PDF conversion started");
			
			SafeMode safe = convertSafe(pdfConfigBean.getSafe());
			
			Attributes attributes = pdfConfigBean.getAsciiDocAttributes();
			
			Asciidoctor doctor = Asciidoctor.Factory.create();
			doctor.requireLibrary("asciidoctor-diagram");
			doctor.javaExtensionRegistry().block(fxChartBlockProcessor);
			doctor.javaExtensionRegistry().block(FooBlockProcessor.class);

			Options options = Options.builder()
			                         .baseDir(destFile.getParentFile())
			                         .toFile(destFile)
			                         .backend("pdf")
			                         .safe(safe)
			                         .sourcemap(pdfConfigBean.getSourcemap())
			                         .headerFooter(pdfConfigBean.getHeader_footer())
			                         .attributes(attributes)
			                         .build();
			doctor.convert(asciidoc,
			               options);

			indikatorService.stopProgressBar();
			logger.debug("PDF conversion ended");

			asciiDocController.addRemoveRecentList(pdfPath);

		});
	}

	private SafeMode convertSafe(String safeStr) {
		if (safeStr == null) {
			return SafeMode.SAFE;
		}
		try {
			return SafeMode.valueOf(safeStr.toUpperCase());
		} catch (IllegalArgumentException ex) {
			logger.error("Unkown safe mode! Will use SAFE.", ex);
			return SafeMode.SAFE;
		}
	}
}
