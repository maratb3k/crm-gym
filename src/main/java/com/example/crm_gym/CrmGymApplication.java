package com.example.crm_gym;

import com.example.crm_gym.config.AppConfig;
import com.example.crm_gym.models.*;
import com.example.crm_gym.services.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import java.util.Date;
import java.util.Optional;

public class CrmGymApplication {

	public static void main(String[] args) {
		GenericApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		UserService userService = context.getBean(UserService.class);
		TraineeService traineeService = context.getBean(TraineeService.class);
		TrainerService trainerService = context.getBean(TrainerService.class);
		TrainingTypeService trainingTypeService = context.getBean(TrainingTypeService.class);
		TrainingService trainingService = context.getBean(TrainingService.class);

		//User user = new User("Sarah", "Lee", "Kate.Middleton", "pas123", true);
		//Trainee trainee = new Trainee(new Date("23.09.2002"), "Astana 2022", user, new HashSet<>(), new HashSet<>());
		//Trainer trainer = new Trainer(Training);
		//TrainingType trainingType = new TrainingType(TrainingTypeName.CARDIO);
		//System.out.println(traineeService.updateTraineeUser("Kate.Middleton", "VYM96r4Vqe", 1L, 1L));
		Optional<User> optUser = userService.getUserById("Sarah.Lee", "lKrgfODBrB", 2L);
		Optional<Trainer> optionalTrainer = trainerService.getTrainerById("Sarah.Lee", "lKrgfODBrB", 1L);
		Optional<Trainee> optionalTrainee = traineeService.getTraineeById("James.Brown", "VoQyDuaoXn", 2L);
		Optional<TrainingType> trainingType = trainingTypeService.getTrainingTypeById("James.Brown", "VoQyDuaoXn",1L);
		//System.out.println(trainerService.create(opt.get()));
		//System.out.println(opt.get());

//		System.out.println(userService.create("Sarah", "Lee"));
//		System.out.println(userService.create("James", "Brown"));

		//System.out.println(trainingTypeService.create(TrainingTypeName.CARDIO));

		//System.out.println(traineeService.updateTraineeUser("Sarah.Lee", "lKrgfODBrB", optionalTrainer.get().getId(), optUser.get().getUserId()));
 		//System.out.println(trainerService.getTrainingsByTrainerUsernameAndCriteria("Sarah.Lee", "lKrgfODBrB", new Date(2024, 9, 10), new Date(2024, 9, 15), optionalTrainee.get().getUser().getUsername()));

		//System.out.println(trainingService.create("cardio group 1", trainingType.get(), new Date(2024, 9, 25), 60));

		//context.close();
	}

}
