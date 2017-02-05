package my.bookstore.payment.commands.impl;

import java.util.Date;

import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.impl.GenericMockCommand;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.AvsStatus;
import de.hybris.platform.payment.dto.CvnStatus;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;


public class SubscriptionAuthorizationMockCommand extends GenericMockCommand implements SubscriptionAuthorizationCommand
{
	public static final String INVALID = "invalid";

	@Override
	public AuthorizationResult perform(final SubscriptionAuthorizationRequest request)
	{
		final AuthorizationResult result = new AuthorizationResult();

		// Let's be as much polite as possible and return the requested data where it makes sense
		result.setCurrency(request.getCurrency());
		result.setTotalAmount(request.getTotalAmount());

		result.setAvsStatus(AvsStatus.NO_RESULT);
		result.setCvnStatus(CvnStatus.NOT_PROCESSED);

		result.setAuthorizationTime(new Date());

		// And the most important is the authorization algorithm ;)
		if (request.getSubscriptionID().equalsIgnoreCase(INVALID))
		{
			result.setTransactionStatus(TransactionStatus.REJECTED);
			result.setTransactionStatusDetails(TransactionStatusDetails.INVALID_SUBSCRIPTION_ID);
		}
		// TODO exercise 19.2: check if the total value of the order is more than 3000; 
		// and if so, reject it!
		else if (request.getTotalAmount().longValue() > 3000)
		{
			result.setTransactionStatus(TransactionStatus.REJECTED);
			result.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED);
		}
		else
		{
			result.setTransactionStatus(TransactionStatus.ACCEPTED);
			result.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
		}

		genericPerform(request, result);

		return result;
	}
}