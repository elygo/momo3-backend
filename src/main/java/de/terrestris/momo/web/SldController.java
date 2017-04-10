package de.terrestris.momo.web;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.momo.service.SldService;
import de.terrestris.shogun2.util.data.ResultSet;

/**
 *
 * terrestris GmbH & Co. KG
 * @author kaivolland
 *
 */
@Controller
@RequestMapping("/sld")
public class SldController {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = Logger.getLogger(SldController.class);

	@Autowired
	@Qualifier("sldService")
	private SldService sldService;

	@RequestMapping(value = "/update.action", method = {RequestMethod.POST})
	public @ResponseBody Map<String, Object> updateSld(
			@RequestParam String sldName,
			@RequestParam String sld,
			@RequestParam Integer layerId) {
		try {
			this.sldService.updateSld(layerId, sldName, sld);
			LOG.debug("Updated Sld " + sldName);
			return ResultSet.success("Updated Sld " + sldName);
		} catch (Exception e) {
			LOG.error("Error while updating Sld: " + e.getMessage());
			return ResultSet.error("Error while updating Sld: " + e.getMessage());
		}
	}

}
