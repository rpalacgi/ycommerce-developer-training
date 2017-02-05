package my.bookstore.core.btg.condition.valueprovider;

import de.hybris.platform.btg.condition.operand.OperandValueProvider;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.jalo.BTGException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import my.bookstore.core.enums.RewardStatusLevel;
import my.bookstore.core.model.BTGCustomerRewardPointsOperandModel;
import my.bookstore.core.model.BookModel;

//TODO exercise 19.2: implement interface!
public class CustomerRewardPointsValueProvider implements OperandValueProvider<BTGCustomerRewardPointsOperandModel>
{

	// TODO exercise 19.2: uncomment and add implementation!
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.btg.condition.operand.OperandValueProvider#getValue(de.hybris.platform.btg.model.BTGOperandModel
	 * , de.hybris.platform.core.model.user.UserModel, de.hybris.platform.btg.enums.BTGConditionEvaluationScope)
	 */
	@Override
	public Object getValue(final BTGCustomerRewardPointsOperandModel arg0, final UserModel user,
			final BTGConditionEvaluationScope arg2)
	{
		//TODO exercise 19.2: add implementation!
		boolean showBanner = false;
		if (user instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) user;
			int totalOrderPoints = 0;
			for (final CartModel cart : customer.getCarts())
			{
				for (final AbstractOrderEntryModel order : cart.getEntries())
				{
					final Object product = order.getProduct();
					if (product instanceof BookModel)
					{
						totalOrderPoints += ((BookModel) product).getRewardPoints() * order.getQuantity();
					}
				}
			}
			if (customer.getPointsToNextLevel() != null && !RewardStatusLevel.GOLD.equals(customer.getRewardStatusLevel())
					&& customer.getPointsToNextLevel() - totalOrderPoints <= 0)
			{
				showBanner = true;
			}
			return showBanner;
		}
		throw new BTGException("user must be of type [Customer]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.btg.condition.operand.OperandValueProvider#getValueType(de.hybris.platform.btg.model.
	 * BTGOperandModel)
	 */
	@Override
	public Class getValueType(final BTGCustomerRewardPointsOperandModel arg0)
	{
		return Boolean.class;
	}
}
