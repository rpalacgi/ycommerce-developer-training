package my.bookstore.payment.commands.impl;

import static org.junit.Assert.fail;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

/**
 * SubscriptionAuthorizationMockCommandTest
 * 
 * The following class supports a Tesd-Driven-Development (TDD) approach
 * for the SAP Hybris Commerce Developer training, part 2.
 *
 * It tests the perform() method of the SubscriptionAuthorizationMockCommand class.
 *
 */
@IntegrationTest
public class SubscriptionAuthorizationMockCommandTest {
	
   /**
    * testPerform
    * 
    *    The authorization command we're testing rejects an order whose amount is over 3000.
    *    
    *    Create an instance of both authorization request and command, and pass the first
    *    as an argument to the second. 
    *    
    *    Since the command rejects orders over 3000, perform boundary testing with the
    *       values 2999, 3000, and 3001. The first two should pass, while the third should fail.
    *       
    */
	
	@Test
	public void testPerform()
	{
		// Create an instance of the SubscriptionAuthorizationMockCommand class
		SubscriptionAuthorizationCommand command = new SubscriptionAuthorizationMockCommand();
		
		// Create parameters for request constructor
		Currency currency = Currency.getInstance("USD");
		BillingInfo shippingInfo = new BillingInfo();

		/* ---------------------------------------------------------------------------------------------------
	      TODO exercise 14.2: Test that the perform() method of the SubscriptionAuthorizationMockCommand class
	                          accepts requests with value up to and including 3000, and rejects requests with
	                          values over 3000 .
		*/

		
		// 1. Create a SubscriptionAuthorizationRequest with value 2999
		BigDecimal totalAmount = new BigDecimal(2999);
		SubscriptionAuthorizationRequest request = 
	       new SubscriptionAuthorizationRequest("VISA", "1", currency, totalAmount, shippingInfo, "PaymentProvider1");

		//    1.1 Test it
		AuthorizationResult result = command.perform(request);

		//    1.2 perform() should return a result with TransactionStatus.ACCEPTED and TransactionStatusDetails.SUCCESSFUL
		if (!result.getTransactionStatus().equals(TransactionStatus.ACCEPTED) || 
		    !result.getTransactionStatusDetails().equals(TransactionStatusDetails.SUCCESFULL))
			fail("The command did not validate a request < 3000.");

		
		// 2. Create a SubscriptionAuthorizationRequest with value 3000
      totalAmount = new BigDecimal(3000);
      request = new SubscriptionAuthorizationRequest("VISA", "1", currency, totalAmount, shippingInfo, "PaymentProvider1");

		//    2.1 Test it
      result = command.perform(request);

		//    2.2 perform() should return a result with TransactionStatus.ACCEPTED and TransactionStatusDetails.SUCCESSFUL
		if (!result.getTransactionStatus().equals(TransactionStatus.ACCEPTED) || 
			!result.getTransactionStatusDetails().equals(TransactionStatusDetails.SUCCESFULL))
			fail("The command did not validate a request = 3000.");
	
		
		// 3. Create a SubscriptionAuthorizationRequest with value 3001
      totalAmount = new BigDecimal(3001);
		request = new SubscriptionAuthorizationRequest("VISA", "1", currency, totalAmount, shippingInfo, "PaymentProvider1");

		//    3.1 Test it
      result = command.perform(request);

		//    3.2 perform() should return a result with TransactionStatus.REJECTED and TransactionStatusDetails.REVIEW_NEEDED
		if (!result.getTransactionStatus().equals(TransactionStatus.REJECTED) || 
			!result.getTransactionStatusDetails().equals(TransactionStatusDetails.REVIEW_NEEDED))
			fail("The command did not reject a request > 3000.");
		
	}
	
	public SubscriptionAuthorizationMockCommandTest() {}
}
