package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * Retrieves a {@link Role} entity by its name.
     *
     * @param name the name of the role to retrieve
     * @return the {@link Role} associated with the provided name
     */
    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new NotFoundException("Role with name " + name + " not found"));
    }
}
