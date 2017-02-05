
package my.bookstore.core.btg.condition.valueprovider;

import static org.junit.Assert.fail;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import my.bookstore.core.enums.RewardStatusLevel;
import my.bookstore.core.model.BTGCustomerRewardPointsOperandModel;
import my.bookstore.core.model.BookModel;


/**
 * DefaultBookstoreCustomerAccountServiceTest
 * 
 * The following class supports a Tesd-Driven-Development (TDD) approach
 * for the SAP Hybris Commerce Developer training, part 1.
 *
 * Test the correct operation of CustomerRewardPointsValueProvider.getValue()
 * This method will be used to conditionally display a banner on the page
 *
 */
@IntegrationTest
public class CustomerRewardPointsValueProviderTest extends ServicelayerTransactionalTest
{
	
	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CustomerRewardPointsValueProvider customerRewardPointsValueProvider;
	
	@Resource
	EnumerationService enumerationService;
	
	private CustomerModel customer;
	private RewardStatusLevel blue; 



	
   /**
    * testUpdateRewardStatusPoints
    * 
    *    The initializeOrder() method creates the following objects:
    *    
    *       Customer - points = 0
    *       
    *       Order - containing the following 2 order entries:
    *                  entry 1: quantity 1, book point value 40
    *                  entry 2: quantity 2, book point value 15
    *    
    */
	
	@Test
	public void testGetValue()
	{
		
	
		/* --------------------------------------------------------------------------------------------------
	      TODO exercise 19.2: Check if the getValue() method of CustomerRewardPointsValueProvider correctly
	                          indicates whether to show a banner when the points in the order are sufficient
	                          to elevate the customer into the next reward level. The threshold values for
	                          reward levels are: 0 (BLUE), 100 (SILVER), and 1000 (GOLD)
	                          
	                          The initialize() method set up the following for you:
	                          
	                          - The cart is configured with 1 product worth 40 reward points, and 2 products
	                            worth 15 points. Value of the order is then 70 points.
	                          - The customer was created with a reward status level of BLUE, and zero points
	                          - The variable 'customer' refers to the current customer
		*/
		
		// 1. Test case where customer IS NOT within reach of reward status level SILVER
		Object rv = customerRewardPointsValueProvider.getValue(new BTGCustomerRewardPointsOperandModel(), customer, BTGConditionEvaluationScope.ONLINE);
		if (!(rv instanceof Boolean))
			fail("CustomerRewardPointsValueProvider.getValue() does not return the expected boolean.");
		// If getValue returns true, that's a problem
		if (((Boolean) rv).booleanValue())
			fail("CustomerRewardPointsValueProvider failed to figure out that the order would not grant enough points to promote customer.");
		
		// 2. Test case where customer IS within reach of reward status level SILVER
		customer.setPoints(50);
		modelService.save(customer);
		rv = customerRewardPointsValueProvider.getValue(new BTGCustomerRewardPointsOperandModel(), customer, BTGConditionEvaluationScope.ONLINE);
		// If getValue returns false, that's a problem
		if (!((Boolean) rv).booleanValue())
			fail("CustomerRewardPointsValueProvider failed to figure out that the order would grant enough points to promote customer.");

	}


	
	
	 /**
	 * Constructor
	 * 
	 *    
	 */
	public CustomerRewardPointsValueProviderTest()
	{
		super();
	}

	
	

	/**
    * initializeOrder
    * 
    *    Create the objects necessary to test the updateRewardStatusPoints() method.
    *    
    *    - Customer
    *    - Cart
    *        Currency
    *        entries
    *           CartEntry1
    *              Book (rewardPoints=40)
    *              quantity=1
    *              Unit
    *           CartEntry2
    *              Book (rewardPoints=15)
    *              quantity=2
    *              Unit
    *    
    */
	@Before
	public void initializeOrder()
	{
      // Initialize variable for the reward status levels
		blue = enumerationService.getEnumerationValue("RewardStatusLevel", "BLUE");


		// Set up past and future dates	
		Calendar past = Calendar.getInstance();
		past.add(Calendar.DAY_OF_MONTH, -2);
		Calendar future = Calendar.getInstance();
		future.add(Calendar.YEAR, 1);

		
		// Create a customer
		customer = modelService.create(CustomerModel.class);
		customer.setUid("walt.whitman@poetry.org");
		customer.setName("Walt Whitman");
		customer.setPoints(0);
		customer.setRewardStatusLevel(blue);
		customer.setExpireDate(future.getTime());
		customer.setRewardLevelStartDate(past.getTime());
		modelService.save(customer);
		
		
		// Find the bookstoreProductCatalog:Staged catalog version to insert products into
		final CatalogVersionModel stagingCatalog = catalogVersionService.getCatalogVersion("bookstoreProductCatalog",  "Staged");
		if (stagingCatalog == null)
			fail("No bookstoreProductCatalog:Staged catalog version found");
		
		// Create a Book with rewardPoints=40
		BookModel book = modelService.create(BookModel.class);
		book.setCode("New and Selected Poems, Volume One");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068780");
		book.setISBN13("9780807068786");
		book.setRentable(true);
		book.setRewardPoints(40);
		modelService.save(book);	

		// Create a Book with rewardPoints=15
		BookModel book2 = modelService.create(BookModel.class);
		book2.setCode("New and Selected Poems, Volume Two");
		book2.setCatalogVersion(stagingCatalog);
		book2.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book2.setLanguage("en");
		book2.setPublisher("Beacon Press");
		book2.setISBN10("0807068861");
		book2.setISBN13("9780807068861");
		book2.setRentable(true);
		book2.setRewardPoints(15);
		modelService.save(book2);	

		// Find the unit 'pieces'
		FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {Unit} WHERE {code} = 'pieces'");
		final List<UnitModel> units = flexibleSearchService.<UnitModel>search(query).getResult();
		if(units.isEmpty())
			fail("Can't locate the 'pieces' Unit");
		UnitModel pieces = units.get(0);		
		

		// Find the currency 'USD'
		query = new FlexibleSearchQuery("SELECT {pk} FROM {Currency} WHERE {isocode} = 'USD'");
		final List<CurrencyModel> currencies = flexibleSearchService.<CurrencyModel>search(query).getResult();
		if(currencies.isEmpty())
			fail("Can't locate the 'USD' Currency");
		CurrencyModel usd = currencies.get(0);
		
		// Create some carts for the customer
		
		Collection<CartModel> carts = new ArrayList<CartModel>();
		
		CartModel cart = modelService.create(CartModel.class);
		cart.setCurrency(usd);
		cart.setDate(new Date());
		cart.setDiscountsIncludeDeliveryCost(false);
		cart.setDiscountsIncludePaymentCost(false);
		cart.setNet(true);
		cart.setUser(customer);
		
		carts.add(cart);

		
		//Create a cart entry for book 1
		CartEntryModel entry = modelService.create(CartEntryModel.class);
		entry.setProduct(book);
		entry.setQuantity(1L);
		entry.setUnit(pieces);
		entry.setGiveAway(false);
		entry.setRejected(false);
		entry.setOrder(cart);

      List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entry);
		
		//Create a cart entry for book 2
		entry = modelService.create(CartEntryModel.class);
		entry.setProduct(book2);
		entry.setQuantity(2L);
		entry.setUnit(pieces);
		entry.setGiveAway(false);
		entry.setRejected(false);
		entry.setOrder(cart);

      entries.add(entry);
		
		// Add the Order Entries to the order and save it
		cart.setEntries(entries);
		modelService.save(cart);
		
		customer.setCarts(carts);
		modelService.save(customer);
		
		
	}

	
}
