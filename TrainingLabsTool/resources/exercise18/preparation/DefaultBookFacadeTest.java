
package my.bookstore.facades.book.impl;


import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.catalog.model.CatalogModel;
import my.bookstore.core.model.BookModel;
import my.bookstore.core.model.RentalModel;
import my.bookstore.facades.book.BookFacade;
import my.bookstore.facades.product.data.BookData;


/**
 * DefaultBookFacadeTest
 *
 * The following class supports a Tesd-Driven-Development (TDD) approach for the SAP Hybris Commerce Developer training,
 * part 2.
 *
 */
@IntegrationTest
public class DefaultBookFacadeTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private BookFacade bookFacade;

	private CustomerModel customer;
	private UserModel author;
	private Date past;
	private Date future;

	final private List<String> trendingBooks = new ArrayList<String>();



	/**
	 * testGetMostRentedBooks
	 *
	 * The initialize() method creates a list you will need for this test:
	 *
	 * trendingBooks - a List<String> containing the ISBN10 values of the top 5 rented books
	 *
	 */
	@Test
	public void testGetTrendingBooks()
	{
		// ----------------------------------------
				// TODO exercise 18.1: Get 5 trending books

				// 1. call getTrendingBooks() method (Ask for 5)
				//...

				//    1.1 Fail if it doesn't return specified number of books (5)
				//...

				//    1.2 Fail if the ISBN10 numbers of the returned list don't match the numbers in trendingBooks.
				//...
	}


	/**
	 * Constructor
	 */
	public DefaultBookFacadeTest()
	{
	}



	/**
	 * initialize
	 *
	 * This method prepares data for your tests. It:
	 *
	 * Creates a user (Mary Oliver), assign her as the author of eight books.
	 *
	 *
	 * We will create multiple rentals for some books, making it possible to determine our five "most rented" books. We
	 * store the five most-rented books in the following list:
	 *
	 * trendingBooks - a List<String> containing ISBN10 values
	 *
	 *
	 * Books 1 & 2 have 3 rentals each Books 3, 4 & 8 have 2 rentals each Books 5, 6 & 7 have 1 rental each
	 *
	 * The five most-rented books will then be books 1, 2, 3, 4, and 8.
	 *
	 */
	@Before
	public void initialize()
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		
		//create the catalog and catalog versions
		final CatalogModel bookstoreProductCatalog = modelService.create(CatalogModel.class);
		bookstoreProductCatalog.setId("bookstoreProductCatalog");
		final CatalogVersionModel bookstoreProductCatalogStaged = modelService.create(CatalogVersionModel.class);
		bookstoreProductCatalogStaged.setCatalog(bookstoreProductCatalog);
		bookstoreProductCatalogStaged.setVersion("Staged");
		final CatalogVersionModel bookstoreProductCatalogOnline = modelService.create(CatalogVersionModel.class);
		bookstoreProductCatalogOnline.setCatalog(bookstoreProductCatalog);
		bookstoreProductCatalogOnline.setVersion("Online");
		modelService.saveAll();
		
		// Create a customer for the rentals
		customer = modelService.create(CustomerModel.class);
		customer.setUid("walt.whitman@poetry.org");
		customer.setName("Walt Whitman");
		modelService.save(customer);

		// Create an author for the books
		author = modelService.create(UserModel.class);
		author.setUid("mary.oliver@poetry.org");
		author.setName("Mary Oliver");

		// Find the bookstoreProductCatalog:Staged catalog version to insert books into
		final CatalogVersionModel stagingCatalog = catalogVersionService.getCatalogVersion("bookstoreProductCatalog", "Staged");
		if (stagingCatalog == null)
		{
			fail("No bookstoreProductCatalog:Staged catalog version found");
		}

		// Delete the existing rentals so we have an accurate count
		// (remember: since we extend ServicelayerTransactionalTest, our changes are rolled back automatically)
		final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {Rental}");
		final List<RentalModel> existingRentals = flexibleSearchService.<RentalModel> search(query).getResult();
		for (final RentalModel rental : existingRentals)
		{
			modelService.remove(rental);
		}

		// Set up rental start/end dates: 1 in the past, 1 in the future
		final Calendar pastCal = Calendar.getInstance();
		pastCal.add(Calendar.DAY_OF_MONTH, -2);
		past = pastCal.getTime();

		final Calendar futureCal = Calendar.getInstance();
		futureCal.add(Calendar.DAY_OF_MONTH, 2);
		future = futureCal.getTime();

		// We'll assign the books to Author when we're done creating them
		final List<BookModel> books = new ArrayList<BookModel>();

		// BOOK 1 -- 3 rentals: 1 active, 2 inactive
		BookModel book = modelService.create(BookModel.class);
		book.setCode("American Primitive");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Back Bay Books");
		book.setISBN10("0316650048");
		book.setISBN13("9780316650045");
		book.setRentable(Boolean.TRUE);
		books.add(book);

		// Create 3 rentals for book 1
		createRental(book, Integer.valueOf(101));
		createRental(book, Integer.valueOf(102));
		createRental(book, Integer.valueOf(103));
		// Book 1 is in our top 5
		trendingBooks.add(book.getISBN10());

		// BOOK 2 -- 3 rentals, in top 5
		book = modelService.create(BookModel.class);
		book.setCode("A Thousand Mornings");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Penguin Press");
		book.setISBN10("1594204772");
		book.setISBN13("9781594204777");
		book.setRentable(Boolean.TRUE);
		books.add(book);

		createRental(book, Integer.valueOf(104));
		createRental(book, Integer.valueOf(105));
		createRental(book, Integer.valueOf(106));
		trendingBooks.add(book.getISBN10());

		// BOOK 3 -- 2 rentals, in top 5
		book = modelService.create(BookModel.class);
		book.setCode("New and Selected Poems, Volume One");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068780");
		book.setISBN13("9780807068786");
		book.setRentable(Boolean.TRUE);
		books.add(book);

		createRental(book, Integer.valueOf(107));
		createRental(book, Integer.valueOf(108));
		trendingBooks.add(book.getISBN10());

		// BOOK 4 -- 2 rentals, in top 5
		book = modelService.create(BookModel.class);
		book.setCode("New and Selected Poems, Volume Two");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068861");
		book.setISBN13("9780807068861");
		book.setRentable(Boolean.TRUE);
		books.add(book);

		createRental(book, Integer.valueOf(109));
		createRental(book, Integer.valueOf(110));
		trendingBooks.add(book.getISBN10());

		// BOOK 5 -- 1 rental
		book = modelService.create(BookModel.class);
		book.setCode("Dog Songs");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Penguin Press");
		book.setISBN10("1594204780");
		book.setISBN13("9781594204784");
		book.setRentable(Boolean.TRUE);
		books.add(book);

		createRental(book, Integer.valueOf(111));

		// BOOK 6 -- 1 rental
		book = modelService.create(BookModel.class);
		book.setCode("A Poetry Handbook");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Mariner Books");
		book.setISBN10("0156724006");
		book.setISBN13("9780156724005");
		book.setRentable(Boolean.TRUE);
		books.add(book);

		createRental(book, Integer.valueOf(112));

		/*
		 * // BOOK 7 -- 1 rental book = modelService.create(BookModel.class); book.setCode("Red Bird: Poems");
		 * book.setCatalogVersion(stagingCatalog); book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		 * book.setLanguage("en"); book.setPublisher("Beacon Press"); book.setISBN10("9780807068922");
		 * book.setISBN13("9780807068922"); book.setRentable(true); books.add(book);
		 *
		 * createRental(book, 113);
		 */

		// BOOK 8 -- 2  rentals, in top 5
		book = modelService.create(BookModel.class);
		book.setCode("Thirst: Poems");
		book.setCatalogVersion(stagingCatalog);
		book.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		book.setLanguage("en");
		book.setPublisher("Beacon Press");
		book.setISBN10("0807068969");
		book.setISBN13("9780807068960");
		book.setRentable(Boolean.TRUE);
		books.add(book);

		createRental(book, Integer.valueOf(114));
		createRental(book, Integer.valueOf(115));
		trendingBooks.add(book.getISBN10());

		// 3. Assign bookList to customer
		author.setBooks(books);

		// 4. Save Customer to db (which also cascade-saves the books)
		modelService.save(author);

	}

	/**
	 * createRental
	 *
	 * Utility method to create a rental for the given book, using the provided rentalId.
	 *
	 * @param book
	 * @param rentalId
	 */
	private void createRental(final BookModel book, final Integer rentalId)
	{
		final RentalModel rental = modelService.create(RentalModel.class);
		rental.setRentalId(rentalId);
		rental.setCustomer(customer);
		rental.setProduct(book);
		rental.setStartDate(past);
		rental.setEndDate(future);
		modelService.save(rental);
	}

}
