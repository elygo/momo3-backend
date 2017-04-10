package de.terrestris.momo.security.access.entity;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.security.Permission;

/**
 * @author Nils Bühner
 *
 */
public class MomoAlwaysAllowReadPermissionEvaluator<E extends PersistentObject> extends
		MomoPersistentObjectPermissionEvaluator<E> {

	/**
	 * Default constructor
	 */
	@SuppressWarnings("unchecked")
	public MomoAlwaysAllowReadPermissionEvaluator() {
		this((Class<E>) PersistentObject.class);
	}

	/**
	 * Constructor for subclasses
	 *
	 * @param entityClass
	 */
	protected MomoAlwaysAllowReadPermissionEvaluator(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * Grants READ permission on the user object of the currently logged in
	 * user. Uses default implementation otherwise.
	 */
	@Override
	public boolean hasPermission(User user, E entity, Permission permission) {

		// always grant READ access ("unsecured" object)
		if (permission.equals(Permission.READ)) {
			LOG.trace("Granting READ access on " + entity.getClass().getSimpleName() + " with ID " + entity.getId());
			return true;
		}

		// call parent implementation
		return super.hasPermission(user, entity, permission);
	}

}
