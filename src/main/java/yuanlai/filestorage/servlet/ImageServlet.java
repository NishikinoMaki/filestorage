package yuanlai.filestorage.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HttpServletBean;

import yuanlai.filestorage.utils.GmtTime;

public abstract class ImageServlet extends HttpServletBean {

	private static final long serialVersionUID = -829372824975411913L;
	private WebApplicationContext spring;

	protected abstract byte[] generateImage(HttpServletRequest request) throws IOException;

	@Override
	protected void initServletBean() throws ServletException {
		spring = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		spring.getAutowireCapableBeanFactory().autowireBeanProperties(this,
				AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
	}

	@Override
	protected final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		byte[] result = null;
		try {
			result = generateImage(request);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		response.setContentType("image/jpeg");
		response.setHeader("Content-Length", Long.toString(result.length));
		// set expires header
		response.setHeader("Expires", GmtTime.toString(DateUtils.addMonths(new Date(), 1)));
		OutputStream output = response.getOutputStream();
		try {
			IOUtils.write(result, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

}
