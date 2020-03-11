package dev.tycho.stonks.command.base.validators;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import org.apache.commons.lang.StringUtils;

public class ServiceValidator extends ArgumentProvider<Service> {

  public ServiceValidator(String name) {
    super(name, Service.class);
  }

  @Override
  public Service provideArgument(String arg) {
    if (!StringUtils.isNumeric(arg)) {
      return null;
    }
    return Repo.getInstance().services().get(Integer.parseInt(arg));
  }

  @Override
  public String getHelp() {
    return "Must be a valid service pk.";
  }
}
