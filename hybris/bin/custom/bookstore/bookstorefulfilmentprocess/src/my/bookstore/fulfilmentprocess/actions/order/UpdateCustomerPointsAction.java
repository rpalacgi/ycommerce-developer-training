/**
 *
 */
package my.bookstore.fulfilmentprocess.actions.order;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;

import my.bookstore.fulfilmentprocess.events.CustomerUpdateEvent;


public class UpdateCustomerPointsAction extends AbstractProceduralAction<OrderProcessModel>
{

	private EventService eventService;

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.processengine.action.AbstractProceduralAction#executeAction(de.hybris.platform.processengine
	 * .model.BusinessProcessModel)
	 */
	@Override
	public void executeAction(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		eventService.publishEvent(new CustomerUpdateEvent(process));
	}
}
