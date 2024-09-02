package com.example.crm_gym;

import com.example.crm_gym.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.example.crm_gym.models.Trainee;
import com.example.crm_gym.services.TraineeService;
import com.example.crm_gym.storage.Storage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class CrmGymApplication {

	public static void main(String[] args) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		Storage storage = context.getBean(Storage.class);
		Map<Integer,Trainee> trainees = storage.getTrainees();
		List<Trainee> traineeList = storage.findAll(trainees);

		TraineeService traineeService = context.getBean(TraineeService.class);

		System.out.println("Contents of traineeStorage:");

		trainees.forEach((id, trainee) -> {
			System.out.println("Trainee ID: " + id + ", Trainee Details: " + trainee);
		});

		try {
			System.out.println("Adding a new Trainee...");

			Trainee newTrainee = new Trainee(
					5, "John", "Doe", "john.doe", "passJohn", true,
					formatter.parse("29-08-1995"), "kkkkk"
			);
			traineeService.createTrainee(newTrainee.getUserId(), newTrainee.getFirstName(), newTrainee.getLastName(), newTrainee.getDateOfBirth(), newTrainee.getAddress());

			System.out.println("Updated contents of traineeStorage:");
			storage.getTrainees().forEach((id, trainee2) -> {
				System.out.println("Trainee ID: " + id + ", Trainee Details: " + trainee2);
			});

		} catch (NumberFormatException e) {
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		context.close();
	}

}
