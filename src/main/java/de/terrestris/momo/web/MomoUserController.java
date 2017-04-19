package de.terrestris.momo.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.momo.dao.MomoUserDao;
import de.terrestris.momo.model.MomoUser;
import de.terrestris.momo.service.MomoUserService;
import de.terrestris.shogun2.util.application.Shogun2ContextUtil;
import de.terrestris.shogun2.util.data.ResultSet;
import de.terrestris.shogun2.web.UserController;

/**
 * @author Johannes Weskamm
 *
 */
@Controller
@RequestMapping("/momousers")
public class MomoUserController<E extends MomoUser, D extends MomoUserDao<E>, S extends MomoUserService<E, D>>
		extends UserController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public MomoUserController() {
		this((Class<E>) MomoUser.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected MomoUserController(Class<E> entityClass) {
		super(entityClass);
	}

	@Autowired
	private String redirectPathAfterActivationError;

	@Autowired
	private String redirectPathAfterRegistrationCompleted;

	/**
	 *
	 * @param token
	 * @return
	 * @return
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/activateAccount.action", method = RequestMethod.GET)
	public void activateMomoUser(
			HttpServletRequest request, HttpServletResponse response, @RequestParam String token) throws URISyntaxException {

		URI appURI = Shogun2ContextUtil.getApplicationURIFromRequest(request);

		try {
			service.activateUser(token);
			// redirect to registration complete page
			response.sendRedirect(appURI + redirectPathAfterRegistrationCompleted);
		} catch(Exception e) {
			final String errorMsgPrefix = "Account could not be activated: ";
			LOG.error(errorMsgPrefix + e.getMessage());
			try {
				response.sendRedirect(appURI + redirectPathAfterActivationError);
			} catch (IOException e2) {
				LOG.error(errorMsgPrefix + e2.getMessage());
			}
		}
	}

	/**
	 *
	 * @param request
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/resendToken.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> resendToken(HttpServletRequest request,
			@RequestParam(value = "email") String email) {
		LOG.debug("Requested to resend the registration mail for '" + email + "'");

		try {
			service.resendRegistrationTokenMail(request, email);
			return ResultSet.success("The registration mail has been sent to your account. "
					+ "Please check your mails!");
		} catch (Exception e) {
			LOG.error("Could not send the registration mail: " + e.getMessage());
			return ResultSet.error("An error has occurred during your request.");
		}
	}

	/**
	 * Updates the users personal credentials and, if a change in permissions
	 * is made, will contact a subadmin / superadmin to make the appropriate changes
	 *
	 * @param request
	 * @param email
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> update(HttpServletRequest request,
			@RequestBody Map<String, Object> params) {
		LOG.debug("Requested to update a user");
		String firstName = (String) params.get("firstName");
		String lastName = (String) params.get("lastName");
		String email = (String) params.get("email");
		String telephone = (String) params.get("telephone");
		String department = (String) params.get("department");
		String profileImage = (String) params.get("profileImage");
		String language = (String) params.get("language");
		HashMap<String, String> permissions = (HashMap<String, String>) params.get("permissions");

		try {
			service.updateUser(firstName, lastName, email, telephone, department,
					profileImage, language, permissions);
			LOG.info("Successfully updated a user");
			return ResultSet.success("ok");
		} catch (Exception e) {
			LOG.error("Could not update the user: " + e.getMessage());
			return ResultSet.error("An error has occurred during your request.");
		}
	}

	/**
	 * Deletes a user and its entities
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> delete(HttpServletRequest request) {

		try {
			service.deleteUser();
			LOG.info("Successfully deleted a user");
			return ResultSet.success("ok");
		} catch (Exception e) {
			LOG.error("Could not delete the user: " + e.getMessage());
			return ResultSet.error(
					"An error has occurred during your deleteion request: " + e.getMessage());
		}
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("momoUserService")
	public void setService(S service) {
		this.service = service;
	}
}
