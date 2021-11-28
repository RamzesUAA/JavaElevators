package di;

import viewModels.BuildingParameters;
import viewModels.BuildingViewModel;
import viewModels.ElevatorViewModel;
import viewModels.PassengerViewModel;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import interactor.BuildingManager;
import interactor.BuildingManagerImpl;
import interactor.PassengerManager;
import interactor.PassengerManagerImpl;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        //managers
        bind(BuildingParameters.class).in(Scopes.SINGLETON);
        bind(BuildingManager.class).to(BuildingManagerImpl.class).in(Scopes.SINGLETON);
        bind(PassengerManager.class).to(PassengerManagerImpl.class).in(Scopes.SINGLETON);

        //viewModels
        bind(BuildingViewModel.class).in(Scopes.SINGLETON);
        bind(ElevatorViewModel.class);
        bind(PassengerViewModel.class);
    }
}
