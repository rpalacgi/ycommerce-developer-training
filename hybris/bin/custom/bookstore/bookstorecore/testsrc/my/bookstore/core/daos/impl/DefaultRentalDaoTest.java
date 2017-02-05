
package my.bookstore.core.daos.impl;

import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.catalog.model.CatalogModel;
import my.bookstore.core.daos.RentalDao;
import my.bookstore.core.model.BookModel;
import my.bookstore.core.model.RentalModel;


/**
 * DefaultRentalDaoTest
 * 
 * The following class supports a Tesd-Driven-Development (TDD) approach
 * for the SAP Hybris Commerce Developer training, part 1.
 *
 * It tests the getActiveRentalsForCustomer() and getMostRentedBooks()
 *    methods of the DefaultRentalDao class.
 *
 */
@IntegrationTest
public class DefaultRentalDaoTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private RentalDao rentalDao;

	private CustomerModel customer;
	private UserModel author;
	

	final private List<Integer> activeRentalIds = new ArrayList<Integer>();
	final private List<Integer> inactiveRentalIds = new ArrayList<Integer>();
	final private List<Integer> foundRentalIds = new ArrayList<Integer>();
   final private List<String> topRentedBooks = new ArrayList<String>();


   /**
    * testGetActiveRentalsForCustomer
    * 
    *    The initialize() method creates sample data for your test.
    *    
    *    It populates the following lists (use them!):
    *    
    *    1. activeRentalIds - a List<Integer> containing the rentalId of active rentals
    *    2. inactiveRentalIds - a List<Integer> containing the rentalId of inactive rentals
    *    
    */

	@Test
	public void testGetActiveRentalsForCustomer()
	{
		/* -----------------------------------------------------------------------
	      Exercise 5.3: Get the list of all the active rentals for this customer.
		                 Test that the returned list contains ALL that customer's
		                 active rentals and NONE of the inactive rentals
		
		   The variable 'customer' refers to the test customer created for you
		*/
		
	   // 1. Call getActiveRentalsForCustomer() method
		List<RentalModel> foundRentals = rentalDao.getActiveRentalsForCustomer(customer);
		
		//    1.1 Fail if returned list includes ANY inactive rentals
		for (RentalModel rental : foundRentals) {
			Integer rentalId = rental.getRentalId();
			foundRentalIds.add(rentalId);
			if (inactiveRentalIds.contains(rentalId))
			   fail("Inactive Rental returned: ID=" + rentalId);
		}

		//    1.2 Fail if returned list doens't contain ALL the active rentals
		for (Integer active : activeRentalIds) {
			if (!foundRentalIds.contains(active))
			   fail("Active Rental not returned: ID=" + active);
			
		}
	}

   /**
    * testGetMostRentedBooks
    * 
    *    The initialize() method creates another list that you will need for this test:
    *    
    *    3. topRentedBooks - a List<String> containing the ISBN10 values of the top 5 rented books
    *    
    */
	@Test
	public void testGetMostRentedBooks()
	{
		// ----------------------------------------------
		// Exercise 5.5: Get the 5 most rented books

		// 2. call getMostRentedBooks() method (Ask for the top 5)
		List<BookModel> fiveMostRented = rentalDao.getMostRentedBooks(5);

		//    2.1 Fail if it doesn't return specified number of books (5)
		if (fiveMostRented.size() != 5)
			fail("The getMostRentedBooks() method did not return the requested number of books");

		//    2.2 Fail if the ISBN10 numbers of the returned list don't match the numbers in topRentedBooks.
		List<String> fiveMostRentedISBN = new ArrayList<String>();		
		for (BookModel book : fiveMostRented) {
			fiveMostRentedISBN.add(book.getISBN10());
		}

		if (!fiveMostRentedISBN.containsAll(topRentedBooks))
			fail("The five most rented books were not returned by the getMostRentedBooks() method");

	}
	
	
	/**
	 * Constructor
	 */
	public DefaultRentalDaoTest() {
		super();
	}
	
	
   /**
    * initialize
    * 
    *    This method prepares data for your tests. It is time-consuming to write,
    *    so we've done the heavy lifting for you. You won't have to edit it for your labs.
    *    
    *    What it does:
    *    
    *    Creates a user (Mary Oliver), assign her as the author of ten books.
    *    
    *    Creates a customer (Walt Whitman) who will be renting these books 
    *    
    *    Creates both  "active rentals" (where the current date falls between the start 
    *    and end dates of the rental) and inactive rentals. The rentalId of these are 
    *    stored in the following lists:
    *       activeRentalIds - a List<Integer> containing the rentalID of active rentals
    *       inactiveRentalIds - a List<Integer> containing the rentalID of inactive rentals
    *    
    *    We will also create multiple rentals for some books, making it possible to 
    *    determine our five "most rented" books. We store the five most-rented books
    *    in the following list:
    *    
    *       topRentedBooks - a List<String> containing ISBN10 values
    *    
    *    
    *    Book 1 has one active & two inactive rentals (total 3)
    *    Book 2 has one active & two inactive rentals (total 3)
    *    Book 3 has one active & one inactive rentals (total 2)
    *    Book 4 has one active & one inactive rentals (total 2)
    *    Book 5 has one inactive rental (total 1)
    *    Book 6 has one inactive rental (total 1)
    *    Book 7 has one inactive rental (total 1)
    *    Book 8 has two inactive rentals (total 2)
    *    
    *    The five most-rented books will then be books 1, 2, 3, 4, and 8.
    *    
    */
	@Before
	public void initialize()
	{
		final CatalogModel bookstoreProductCatalog = modelService.create(CatalogModel.class);
		bookstoreProductCatalog.setId("bookstoreProductCatalog");
		final CatalogVersionModel bookstoreProductCatalogStaged = modelService.create(CatalogVersionModel.class);
		bookstoreProductCatalogStaged.setCatalog(bookstoreProductCatalog);
		bookstoreProductCatalogStaged.setVersion("Staged");
		final CatalogVersionModel bookstoreProductCatalogOnline = modelService.create(CatalogVersionModel.class);
		bookstoreProductCatalogOnline.setCatalog(bookstoreProductCatalog);
		bookstoreProductCatalogOnline.setVersion("Online");
		modelService.saveAll();
		
		// Create an author for the books
		author = modelService.create(UserModel.class);
		author.setUid("mary.oliver@poetry.org");
		author.setName("Mary Oliver");
		
		// Create a customer for the rentals
		customer = modelService.create(CustomerModel.class);
		customer.setUid("walt.whitman@poetry.org");
		customer.setName("Walt Whitman");
		modelService.save(customer);

		// Find the bookstoreProductCatalog:Staged catalog version to insert books into	
		final CatalogVersionModel stagingCatalog = catalogVersionService.getCatalogVersion("bookstoreProductCatalog",  "Staged");
		if (stagingCatalog == null)
			fail("No bookstoreProductCatalog:Staged catalog version found");
		
		// Delete the existing rentals so we have an accurate count	
		// (remember: since we extend ServicelayerTransactionalTest, our changes are rolled back automatically)
		final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {Rental}");
		final List<RentalModel> existingRentals = flexibleSearchService.<RentalModel>search(query).getResult();
		for (RentalModel rental : existingRentals) {
			modelService.remove(rental);
		}
		
		// Set up rental start/end dates: 2 in the past, 2 in the future	
		Calendar past = Calendar.getInstance();
		past.add(Calendar.DAY_OF_MONTH, -2);
		Calendar farPast = Calendar.getInstance();
		farPast.add(Calendar.MONTH, -1);
		Calendar future = Calendar.getInstance();
		future.add(Calendar.DAY_OF_MONTH, 2);
		Calendar farFuture = Calendar.getInstance();
		farFuture.add(Calendar.MONTH, 1);

		// We'll assign the books to Author when we're done creating them
	   List<BookModel> books = new ArrayList();
		
		// BOOK 1 -- 3 rentals: 1 active, 2 inactive
		BookModel book = modelService.create(BookModel.class);
		book.setCode("American Primitive");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Back Bay Books");
		book.setISBN10("0316650048");
		book.setISBN13("9780316650045");
		book.setRentable(true);
		books.add(book);
		
		// Active rental (book 1)
		RentalModel rental = modelService.create(RentalModel.class);
		rental.setRentalId(101);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(past.getTime());
		rental.setEndDate(future.getTime());
		modelService.save(rental);
		activeRentalIds.add(rental.getRentalId());
				
		// Inactive rentals (book 1)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(102);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(103);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(future.getTime());
		rental.setEndDate(farFuture.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		// Book 1 is in our top 5
		topRentedBooks.add(book.getISBN10());
		
		
		// BOOK 2 -- 3 rentals: 1 active, 2 inactive
		book = modelService.create(BookModel.class);
		book.setCode("A Thousand Mornings");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Penguin Press");
		book.setISBN10("1594204772");
		book.setISBN13("9781594204777");
		book.setRentable(true);
		books.add(book);
		
		// Active rental (book 2)
	   rental = modelService.create(RentalModel.class);
		rental.setRentalId(104);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(past.getTime());
		rental.setEndDate(future.getTime());
		modelService.save(rental);
		activeRentalIds.add(rental.getRentalId());
				
		// Inactive rentals (book 2)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(105);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(106);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(future.getTime());
		rental.setEndDate(farFuture.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		// Book 2 is in our top 5
		topRentedBooks.add(book.getISBN10());
		
		
		// BOOK 3 -- 2 rentals: 1 active, 1 inactive
		book = modelService.create(BookModel.class);
		book.setCode("New and Selected Poems, Volume One");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068780");
		book.setISBN13("9780807068786");
		book.setRentable(true);
		books.add(book);

		// Active rental (book 3)
	   rental = modelService.create(RentalModel.class);
		rental.setRentalId(107);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(past.getTime());
		rental.setEndDate(future.getTime());
		modelService.save(rental);
		activeRentalIds.add(rental.getRentalId());
				
		// Inactive rental (book 3)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(108);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		// Book 3 is in our top 5
		topRentedBooks.add(book.getISBN10());
		
		
		// BOOK 4 -- 2 rentals: 1 active, 1 inactive
		book = modelService.create(BookModel.class);
		book.setCode("New and Selected Poems, Volume Two");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068861");
		book.setISBN13("9780807068861");
		book.setRentable(true);
		books.add(book);
		
		// Active rental (book 4)
	   rental = modelService.create(RentalModel.class);
		rental.setRentalId(109);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(past.getTime());
		rental.setEndDate(future.getTime());
		modelService.save(rental);
		activeRentalIds.add(rental.getRentalId());
				
		// Inactive rental (book 4)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(110);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		// Book 4 is in our top 5
		topRentedBooks.add(book.getISBN10());
		
		
		// BOOK 5 -- 1 inactive rental
		book = modelService.create(BookModel.class);
		book.setCode("Dog Songs");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Penguin Press");
		book.setISBN10("1594204780");
		book.setISBN13("9781594204784");
		book.setRentable(true);
		books.add(book);
		
		// Inactive rental (book 5)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(111);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());

		
		// BOOK 6 -- 1 inactive rental
		book = modelService.create(BookModel.class);
		book.setCode("A Poetry Handbook");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Mariner Books");
		book.setISBN10("0156724006");
		book.setISBN13("9780156724005");
		book.setRentable(true);
		books.add(book);
		
		// Inactive rental (book 6)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(112);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		
		// BOOK 7 -- 1 inactive rental	
		book = modelService.create(BookModel.class);
		book.setCode("Red Bird: Poems");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068934");
		book.setISBN13("9780807068922");
		book.setRentable(true);
		books.add(book);
		
		// Inactive rental (book 7)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(113);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		
       // BOOK 8 -- 2 inactive rentals
		book = modelService.create(BookModel.class);
		book.setCode("Thirst: Poems");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068969");
		book.setISBN13("9780807068960");
		book.setRentable(true);
		books.add(book);

		// Inactive rentals (book 8)
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(114);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(farPast.getTime());
		rental.setEndDate(past.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		rental = modelService.create(RentalModel.class);
		rental.setRentalId(115);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(future.getTime());
		rental.setEndDate(farFuture.getTime());
		modelService.save(rental);
		inactiveRentalIds.add(rental.getRentalId());
		
		// Book 8 is in our top 5
		topRentedBooks.add(book.getISBN10());
		
		
		// BOOK 9
		book = modelService.create(BookModel.class);
		book.setCode("Dream Works");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Atlantic Monthly Press");
		book.setISBN10("0871130718");
		book.setISBN13("9780871130716");
		book.setRentable(true);
		books.add(book);

		
		// BOOK 10
		book = modelService.create(BookModel.class);
		book.setCode("House of Light");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068101");
		book.setISBN13("9780807068106");
		book.setRentable(true);
		books.add(book);

		
		// 3. Assign bookList to customer
		author.setBooks(books);

		// 4. Save Customer to db (which also cascade-saves the books)
		modelService.save(author);

	}
	
 
}
