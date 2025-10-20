package profect.group1.goormdotcom.delivery.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import profect.group1.goormdotcom.delivery.repository.DeliveryRepository;
import profect.group1.goormdotcom.delivery.repository.DeliveryReturnRepository;
import profect.group1.goormdotcom.delivery.repository.DeliveryAddressRepository;
import profect.group1.goormdotcom.delivery.repository.DeliveryReturnAddressRepository;
import profect.group1.goormdotcom.delivery.repository.DeliveryStepHistoryRepository;
import profect.group1.goormdotcom.delivery.repository.DeliveryReturnStepHistoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

	private final DeliveryRepository deliveryRepo;
	private final DeliveryReturnRepository deliveryReturnRepo;
	private final DeliveryAddressRepository deliveryAddressRepo;
	private final DeliveryReturnAddressRepository deliveryReturnAddressRepo;
	private final DeliveryStepHistoryRepository deliveryStepHistoryRepo;
	private final DeliveryReturnStepHistoryRepository deliveryReturnStepHistoryRepo;

}
