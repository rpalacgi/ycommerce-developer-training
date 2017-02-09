package my.bookstore.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

import my.bookstore.core.daos.RentalDao;
import my.bookstore.core.model.BookModel;
import my.bookstore.core.services.BookstoreEmailService;


public class BookstoreEmailJob // TODO exercise 16.1 : extend appropriate class
{

	private BookstoreEmailService bookstoreEmailService;
	private RentalDao rentalDao;

	
	public PerformResult perform(final CronJobModel cronJob)
	{
		// TODO exercise 16.1 : add implementation;
		
		return null;
	}

}
