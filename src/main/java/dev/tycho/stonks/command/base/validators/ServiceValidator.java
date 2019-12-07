package dev.tycho.stonks.command.base.validators;

import dev.tycho.stonks.managers.Repo;
import dev.tycho.stonks.model.service.Service;
import org.apache.commons.lang.StringUtils;

public class ServiceValidator extends ArgumentValidator<Service> {
  public ServiceValidator(String name) {
    super(name);
  }

  @Override
  public boolean provide(String str) {
    if (!StringUtils.isNumeric(str)) return false;
    value = Repo.getInstance().services().get(Integer.parseInt(str));
    return value != null;
  }

  @Override
  public String getPrompt() {
    return "must be a valid service id";
  }
}
