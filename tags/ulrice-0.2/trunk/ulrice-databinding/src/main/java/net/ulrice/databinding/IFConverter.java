package net.ulrice.databinding;

/**
 * Generic converter interface.
 * 
 * @author christof
 */
public interface IFConverter<Source, Target> {

	/** Map the target to the source. */
	Source mapToSource(Target target);
	
	/** Map the source to the target. */
	Target mapToTarget(Source source);	
}
